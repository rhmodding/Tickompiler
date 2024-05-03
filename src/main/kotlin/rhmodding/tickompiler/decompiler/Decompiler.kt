package rhmodding.tickompiler.decompiler

import rhmodding.tickompiler.BytesFunction
import rhmodding.tickompiler.Function
import rhmodding.tickompiler.Functions
import rhmodding.tickompiler.Tickompiler.GITHUB
import rhmodding.tickompiler.Tickompiler.VERSION
import rhmodding.tickompiler.util.escape
import java.io.ByteArrayInputStream
import java.nio.ByteOrder
import java.util.*
import kotlin.math.max

class Decompiler(val array: ByteArray, val order: ByteOrder, val functions: Functions) {
    var input = ByteArrayInputStream(array)

    private fun read(): Long {
        val r = input.read()
        if (r == -1)
            throw IllegalStateException("End of stream reached")
        return r.toLong()
    }

    private fun readInt(): Long {
        return if (order == ByteOrder.LITTLE_ENDIAN) {
            read() or (read() shl 8) or (read() shl 16) or (read() shl 24)
        } else {
            (read() shl 24) or (read() shl 16) or (read() shl 8) or read()
        }
    }

    private fun readStringAuto(): Pair<String, Int> {
        var result = ""
        var i = 2
        var r = read()
        if (r == 0L) {
            return "" to 1
        }
        var u = false
        result += r.toChar()
        r = read()
        if (r == 0L) {
            u = true
            r = read()
            read()
            i += 2
        }
        while (r != 0L) {
            result += r.toChar()
            r = read()
            i++
            if (u) {
                read()
                i++
            }
        }
        while (i % 4 != 0) {
            read()
            i++
        }
        return Pair(result, i)
    }

    fun decompile(addComments: CommentType, useMetadata: Boolean, indent: String = "\t", macros: Map<Int, Int> = mapOf()): Pair<Double, String> {
        val nanoTime = System.nanoTime()
        val builder = StringBuilder()
        val state = DecompilerState()

        run decompilerInfo@ {
            builder.append("// Decompiled using Tickompiler $VERSION\n// $GITHUB\n")
        }
        val markers = mutableMapOf<Long, String>()

        if (useMetadata) {
            builder.append("#index 0x${readInt().toString(16).toUpperCase()}\n")
            val startPos = readInt()
            markers[readInt()] = "assets"
            markers[startPos] = "start"
        }

        for ((key, value) in macros) {
            markers[key.toLong()] = "sub${value.toString(16).toUpperCase()}"
        }
        var markerC = 0
        val strings = mutableMapOf<Long, String>()

        var counter = 0L
        // first pass is to construct a list of markers:
        while (input.available() > 0) {
            val anns = mutableListOf<Long>()
            var opint: Long = readInt()
            if (opint == 0xFFFFFFFE) {
                break
            }
            if (opint == 0xFFFFFFFF) {
                val amount = readInt()
                var bytes = 0L
                for (i in 1..amount) {
                    val ann = readInt()
                    if (ann and 0xFF == 3L) {
                        bytes = ann ushr 8
                    }
                    else {
                        anns.add(ann)
                    }
                }
                if (bytes != 0L) {
                    if (bytes % 4 == 0L)
                        bytes += 4 - bytes % 4
                    counter += bytes_padded
                    for (i in 1..bytes_padded) {
                        read()
                    }
                    continue
                }
                opint = readInt()
            }
            val opcode: Long = opint and 0b1111111111
            val special: Long = (opint ushr 14)
            val argCount: Long = (opint ushr 10) and 0b1111
            val args: MutableList<Long> = mutableListOf()

            if (argCount > 0) {
                for (i in 1..argCount) {
                    args.add(readInt())
                }
            }

            anns.forEach {
                val anncode = it and 0xFF
                val annArg = (it ushr 8).toInt()
                if (anncode == 0L) {
                    if (!markers.contains(args[annArg])) {
                        markers[args[annArg]] = "loc${markerC++}"
                    }
                }
            }

            counter += 4 * (1 + argCount)
        }

        // and also strings
        while (input.available() > 0) {
            val p = readStringAuto()
            strings[counter] = p.first.escape()
            counter += p.second
        }

        // reset input stream for the second pass:
        input = ByteArrayInputStream(array)
        if (useMetadata) {
            input.skip(12)
        }

        counter = 0L

        while (input.available() > 0) {
            if (markers.contains(counter)) {
                builder.append("${markers[counter]}:\n")
            }

            val specialArgStrings: MutableMap<Int, String> = mutableMapOf()

            val anns = mutableListOf<Long>()
            var opint: Long = readInt()
            if (opint == 0xFFFFFFFE) {
                break
            }
            if (opint == 0xFFFFFFFF) {
                val amount = readInt()
                for (i in 1..amount) {
                    anns.add(readInt())
                }
            }

            var bytes = 0
            anns.forEach {
                if ((it and 0xFF) == 3L) {
                    bytes = (it ushr 8).toInt()
                }
            }
            if (bytes > 0) {
                val padding = 4 - (bytes % 4) - (if (bytes % 4 == 0) 4 else 0)
                counter += bytes + padding
                val byteList = mutableListOf<Long>()
                for (i in 1..bytes) {
                    byteList.add(read())
                }
                for (i in 1..padding) {
                    read()
                }
                val function: Function = BytesFunction()
                val tickflow = function.produceTickflow(state, 0, 0, byteList.toLongArray(), addComments, specialArgStrings)
                for (i in 1..state.nextIndentLevel) {
                    builder.append(indent)
                }
                builder.append(tickflow + "\n")
                continue
            }

            if (opint == 0xFFFFFFFF) {
                opint = readInt()
            }
            val opcode: Long = opint and 0b1111111111
            val special: Long = (opint ushr 14)
            val argCount: Long = (opint ushr 10) and 0b1111
            val function: Function = functions[opint]

            val args: MutableList<Long> = mutableListOf()

            if (argCount > 0) {
                for (i in 1..argCount) {
                    args.add(readInt())
                }
            }

            anns.forEach {
                val anncode = it and 0b11111111
                val annArg = (it ushr 8).toInt()
                when(anncode) {
                    0L -> specialArgStrings[annArg] = markers[args[annArg]] ?: args[annArg].toString()
                    1L -> specialArgStrings[annArg] = "u\"" + (strings[args[annArg]] ?: "") + '"'
                    2L -> specialArgStrings[annArg] = '"' + (strings[args[annArg]] ?: "") + '"'
                }
            }

            val oldIndent = state.nextIndentLevel
            val tickFlow = function.produceTickflow(state, opcode, special, args.toLongArray(), addComments,
                                                    specialArgStrings)

            for (i in 1..(oldIndent + state.currentAdjust)) {
                builder.append(indent)
            }

            builder.append(tickFlow)
            if (addComments == CommentType.BYTECODE) {
                fun Int.toLittleEndianHex(): String {
                    return toString(16).padStart(8, '0').toUpperCase(Locale.ROOT)
                }

                builder.append(" // bytecode: ${opint.toInt().toLittleEndianHex()} ${args.joinToString(" "){it.toInt().toLittleEndianHex()}}")
            }
            builder.append('\n')
            state.currentAdjust = 0
            state.nextIndentLevel = max(state.nextIndentLevel, 0)
            counter += 4 * (1 + argCount)
        }

        return ((System.nanoTime() - nanoTime) / 1_000_000.0) to builder.toString()
    }

}

data class DecompilerState(var nextIndentLevel: Int = 0, var currentAdjust: Int = 0)

enum class CommentType {
    NONE, NORMAL, BYTECODE
}
