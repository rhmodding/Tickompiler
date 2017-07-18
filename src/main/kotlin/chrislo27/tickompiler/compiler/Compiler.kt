package chrislo27.tickompiler.compiler

import chrislo27.tickompiler.CompilerError
import chrislo27.tickompiler.Function
import chrislo27.tickompiler.Functions
import chrislo27.tickompiler.MegamixFunctions
import org.parboiled.Parboiled
import org.parboiled.parserunners.RecoveringParseRunner
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

    fun compileStatement(statement: Any, longs: MutableList<Long>, variables: MutableMap<String, Long>) {
        when (statement) {
            is FunctionCallNode -> {
                val funcCall = FunctionCall(statement.func, statement.special?.getValue(variables) ?: 0,
                                            statement.args.map { it.getValue(variables) })

                val function: Function = functions[funcCall.func]

                function.checkArgsNeeded(funcCall)
                function.produceBytecode(funcCall).forEach { longs.add(it) }
            }
            is VarAssignNode -> {
                variables[statement.variable] = statement.expr.getValue(variables)
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

    constructor(file: File) : this(preProcess(file), MegamixFunctions)
    constructor(file: File, functions: Functions) : this(preProcess(file), functions)


    fun compile(endianness: ByteOrder): CompileResult {
        startNanoTime = System.nanoTime()

        // Split tickflow into lines, stripping comments
        val commentLess = tickflow.lines()
                .map {
                    it.replaceAfter("//", "").replace("//", "").trim()
                }.joinToString("\n")

        val parser = Parboiled.createParser(TickflowParser::class.java)
        val result = RecoveringParseRunner<Any>(parser.TickflowCode()).run(commentLess)

//		println(ParseTreeUtils.printNodeTree(result))

        // TODO optimize primitives?
        val longs: MutableList<Long> = mutableListOf()
        val variables: MutableMap<String, Long> = mutableMapOf()

//		result.valueStack.reversed().forEach(::println)
        var counter = 0L
        val startMetadata = MutableList<Long>(3, { 0 })
        var hasMetadata = false
        result.valueStack.reversed().forEach {
            when (it) {
                is AliasAssignNode -> functions[it.expr.getValue(variables)] = it.alias
                is FunctionCallNode -> {
                    val funcCall = FunctionCall(it.func, 0,
                                                it.args.map { 0L })
                    val function: Function = functions[funcCall.func]
                    val len = function.produceBytecode(funcCall).size
                    counter += len * 4
                }
                is MarkerNode -> {
                    variables[it.name] = counter
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

        result.valueStack.reversed().forEach {
            compileStatement(it, longs, variables)
        }
        val buffer = ByteBuffer.allocate(longs.size * 4 + (if (hasMetadata) 12 else 0))
        // invert because java is big endian or something like that
        buffer.order(if (endianness == ByteOrder.BIG_ENDIAN) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        if (hasMetadata) {
            startMetadata.forEach { buffer.putInt(it.toInt()) }
        }
        longs.forEach { buffer.putInt(it.toInt()) }

        return CompileResult(result.matched, (System.nanoTime() - startNanoTime) / 1_000_000.0, buffer)
    }

}

private fun preProcess(file: File): String {
    val tickflow = file.readText(Charset.forName("UTF-8"))
    val newTickflow = tickflow.lines().map {
        if (it.startsWith("#include")) {
            val filename = it.split(" ")[1]
            val otherfile = File(file.parentFile, filename)
            if (otherfile.exists() && otherfile.isFile) {
                otherfile.readText(Charset.forName("UTF-8"))
            } else {
                throw CompilerError("Included file $filename not found.")
            }
        } else it
    }.joinToString("\n")
    return newTickflow
}

data class CompileResult(val success: Boolean, val timeMs: Double, val data: ByteBuffer)

data class FunctionCall(val func: String, val specialArg: Long, val args: List<Long>)
