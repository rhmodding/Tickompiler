package rhmodding.tickompiler.compiler

import org.parboiled.Parboiled
import org.parboiled.parserunners.RecoveringParseRunner
import rhmodding.tickompiler.*
import rhmodding.tickompiler.Function
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class Compiler(val tickflow: String, val functions: Functions) {
    private var hasStartedTiming = false
    private var startNanoTime: Long = 0
        set(value) {
            if (!hasStartedTiming) {
                field = value
                hasStartedTiming = true
            }
        }

    enum class VariableType {
        VARIABLE,
        MARKER,
        STRING
    }

    fun unicodeStringToInts(str: String, ordering: ByteOrder): List<Long> {
        val result = mutableListOf<Long>()
        var i = 0
        while (i <= str.length) {
            var int = 0
            if (i < str.length)
                int += str[i].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 16 else 0)
            if (i + 1 < str.length)
                int += str[i + 1].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 0 else 16)
            i += 2
            result.add(int.toLong())
        }
        return result
    }

    fun stringToInts(str: String, ordering: ByteOrder): List<Long> {
        val result = mutableListOf<Long>()
        var i = 0
        while (i <= str.length) {
            var int = 0
            if (i < str.length)
                int += str[i].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 24 else 0)
            if (i + 1 < str.length)
                int += str[i + 1].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 16 else 8)
            if (i + 2 < str.length)
                int += str[i + 2].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 8 else 16)
            if (i + 3 < str.length)
                int += str[i + 3].toByte().toInt() shl (if (ordering == ByteOrder.LITTLE_ENDIAN) 0 else 24)
            i += 4
            result.add(int.toLong())
        }
        return result
    }

    fun compileStatement(statement: Any, longs: MutableList<Long>, variables: MutableMap<String, Pair<Long, VariableType>>) {
        when (statement) {
            is FunctionCallNode -> {
                val argAnnotations = mutableListOf<Pair<Int, Int>>()
                val funcCall = FunctionCall(statement.func,
                                            statement.special?.getValue(variables) ?: 0,
                                            statement.args.mapIndexed { index, it ->
                                                if (it.type == ExpType.VARIABLE && variables[it.id as String]?.second == VariableType.MARKER) {
                                                    argAnnotations.add(Pair(index, 0))
                                                }
                                                if (it.type == ExpType.USTRING) {
                                                    argAnnotations.add(Pair(index, 1))
                                                }
                                                if (it.type == ExpType.STRING) {
                                                    argAnnotations.add(Pair(index, 2))
                                                }
                                                it.getValue(variables)
                                            })

                if (argAnnotations.size > 0) {
                    longs.add(0xFFFFFFFF)
                    longs.add(argAnnotations.size.toLong())
                    argAnnotations.forEach {
                        longs.add((it.second + (it.first shl 8)).toLong())
                    }
                }
                val function: Function = functions[funcCall.func]

                if (function::class.java.isAnnotationPresent(DeprecatedFunction::class.java)) {
                    println("DEPRECATION WARNING at ${statement.position.line}:${statement.position.column} -> " +
                                    function::class.java.annotations.filterIsInstance<DeprecatedFunction>().first().value)
                }

                function.checkArgsNeeded(funcCall)
                function.produceBytecode(funcCall).forEach { longs.add(it) }
            }
            is VarAssignNode -> {
                variables[statement.variable] = statement.expr.getValue(variables) to VariableType.VARIABLE
            }
        /*is LoopNode -> {
            (1..statement.expr.getValue(variables)).forEach {
                statement.statements.forEach {
                    compileStatement(it, longs, variables)
                }
            }
        }*/
        }
    }

    constructor(file: File) : this(preProcess(file),
                                   MegamixFunctions)

    constructor(file: File, functions: Functions) : this(
            preProcess(file), functions)


    fun compile(endianness: ByteOrder): CompileResult {
        startNanoTime = System.nanoTime()

        // Split tickflow into lines, stripping comments
        val commentLess = tickflow.lines().joinToString("\n") {
            it.replaceAfter("//", "").replace("//", "").trim()
        }

        val parser = Parboiled.createParser(TickflowParser::class.java)
        val result = RecoveringParseRunner<Any>(parser.TickflowCode()).run(commentLess)

//		println(ParseTreeUtils.printNodeTree(result))

        // TODO optimize primitives?
        val longs: MutableList<Long> = mutableListOf()
        val variables: MutableMap<String, Pair<Long, VariableType>> = mutableMapOf()

//		result.valueStack.reversed().forEach(::println)
        var counter = 0L
        val startMetadata = MutableList<Long>(3, { 0 })
        var hasMetadata = false
        val ustrings = mutableListOf<String>()
        val strings = mutableListOf<String>()
        result.valueStack.reversed().forEach {
            when (it) {
                is AliasAssignNode -> functions[it.expr.getValue(variables)] = it.alias
                is FunctionCallNode -> {
                    val funcCall = FunctionCall(it.func, 0,
                                                it.args.map {
                                                    if (it.type == ExpType.STRING) {
                                                        strings.add(it.string as String)
                                                    }
                                                    if (it.type == ExpType.USTRING) {
                                                        ustrings.add(it.string as String)
                                                    }
                                                    0L
                                                })
                    val function: Function = functions[funcCall.func]
                    val len = function.produceBytecode(funcCall).size
                    counter += len * 4
                }
                is MarkerNode -> {
                    variables[it.name] = counter to VariableType.MARKER
                }
                is DirectiveNode -> {
                    hasMetadata = true
                    when (it.name) {
                        "index" -> startMetadata[0] = it.num
                        "start" -> startMetadata[1] = it.num
                        "assets" -> startMetadata[2] = it.num
                    }
                }
            }
        }

        ustrings.forEach {
            variables[it] = counter to VariableType.STRING
            counter += unicodeStringToInts(it, endianness).size * 4
        }
        strings.forEach {
            variables[it] = counter to VariableType.STRING
            counter += stringToInts(it, endianness).size * 4
        }

        result.valueStack.reversed().forEach {
            compileStatement(it, longs, variables)
        }
        longs.add(0xFFFFFFFE)
        ustrings.forEach {
            longs.addAll(unicodeStringToInts(it, endianness))
        }
        strings.forEach {
            longs.addAll(stringToInts(it, endianness))
        }
        val buffer = ByteBuffer.allocate(longs.size * 4 + (if (hasMetadata) 12 else 0))
        // invert because java is big endian or something like that
        buffer.order(if (endianness == ByteOrder.BIG_ENDIAN) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        if (hasMetadata) {
            startMetadata.forEach { buffer.putInt(it.toInt()) }
        }
        longs.forEach { buffer.putInt(it.toInt()) }

        return CompileResult(result.matched,
                             (System.nanoTime() - startNanoTime) / 1_000_000.0, buffer)
    }

}

private fun preProcess(file: File): String {
    val tickflow = file.readText(Charset.forName("UTF-8"))
    val newTickflow = tickflow.lines().joinToString("\n") {
        if (it.startsWith("#include")) {
            val filename = it.split(" ")[1]
            val otherfile = File(file.parentFile, filename)
            if (otherfile.exists() && otherfile.isFile) {
                otherfile.readText(Charset.forName("UTF-8"))
            } else {
                throw CompilerError("Included file $filename not found.")
            }
        } else it
    }
    return newTickflow
}

data class CompileResult(val success: Boolean, val timeMs: Double, val data: ByteBuffer)

data class FunctionCall(val func: String, val specialArg: Long, val args: List<Long>)
