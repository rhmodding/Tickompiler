package rhmodding.tickompiler.gameextractor

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class GameExtractor(val allSubs: Boolean) {

    private var engine = -1
    private var isRemix = false
    private var strings = mutableListOf<Pair<Int, String>>()

    private val STRING_OPS = mutableMapOf(
            0x31 to 0 to 1,
            0x35 to 0 to 1,
            0x39 to 0 to 1,
            0x3E to 0 to 1,
            0x5D to 0 to 1,
            0x5D to 2 to 0
    )

    companion object {
        val LOCATIONS: List<List<Int>> by lazy {
            Gson().fromJson<List<List<Int>>>(
                    GameExtractor::class.java.getResource("/locations.json").readText())
        }
    }

    fun stringToInts(str: String): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0
        while (i <= str.length) {
            var int = 0
            if (i < str.length)
                int += str[i].toByte().toInt() shl 0
            if (i+1 < str.length)
                int += str[i+1].toByte().toInt() shl 8
            if (i+2 < str.length)
                int += str[i+2].toByte().toInt() shl 16
            if (i+3 < str.length)
                int += str[i+3].toByte().toInt() shl 24
            i += 4
            result.add(int)
        }
        return result
    }

    fun extractGame(buffer: ByteBuffer, index: Int): Pair<Map<Int, Int>, List<Int>> {
        val start = buffer.getStart(index)
        engine = -1
        val funcs = firstPass(buffer, start)
        val sorted: List<Pair<Int, List<Int>>>
        if (!isRemix) {
            sorted = mutableListOf(funcs[0])
            sorted.addAll(funcs.drop(1).sortedBy {
                if (it.first in LOCATIONS[engine]) {
                    LOCATIONS[engine].indexOf(it.first)
                } else {
                    1000
                }
            })
        } else {
            sorted = funcs
        }
        val returnMap = mutableMapOf<Int, Int>()
        val map = mutableMapOf<Int, Int>()
        val stringMap = mutableMapOf<String, Int>()
        var i = 0
        for ((first, second) in sorted) {
            map[first] = i
            if (!isRemix && first in LOCATIONS[engine]) {
                returnMap[i] = LOCATIONS[engine].indexOf(first) + 0x56
            }
            i += second.size * 4
        }
        for ((first, second) in strings) {
            map[first] = i
            i += stringToInts(second).size * 4
        }
        val meta = mutableListOf<Int>()
        meta.add(index)
        meta.add(0)
        meta.add(map[buffer.getIntAdj(TABLE_OFFSET + 52 * index + 8)] ?: 0)
        return returnMap to (meta + secondPass(sorted.map { it.second }, map))
    }

    fun secondPass(funcs: List<List<Int>>, map: Map<Int, Int>): List<Int> {
        val result = mutableListOf<Int>()
        for (l in funcs) {
            var i = 0
            while (i < l.size) {
                val opint = l[i]
                val opcode = opint and 0b1111111111
                val special = (opint ushr 14)
                val argCount = (opint ushr 10) and 0b1111
                val args = l.slice(i+1..i+argCount).toMutableList()
                i += argCount + 1
                if (opcode == 2 || opcode == 6) {
                    result.addAll(listOf(-1, 1, 0))
                    args[0] = map[args[0]] ?: 0
                }
                if (opcode == 1) {
                    result.addAll(listOf(-1, 1, 0x100))
                    args[1] = map[args[1]] ?: 0
                }
                if (opcode to special in STRING_OPS) {
                    val n = STRING_OPS[opcode to special]?:0
                    result.addAll(listOf(-1, 1, 1 or (n shl 8)))
                    args[n] = map[args[n]] ?: 0
                }
                result.add(opint)
                result.addAll(args)
            }
        }
        result.add(-2)
        result.addAll(strings.map {stringToInts(it.second)}.flatten())
        return result
    }

    fun firstPass(buf: ByteBuffer, start: Int): List<Pair<Int, List<Int>>> {
        val result = mutableListOf<Pair<Int, List<Int>>>()
        strings = mutableListOf()

        isRemix = buf.getIntAdj(start) and 0b1111111111 == 1
        val q = ArrayDeque<Int>()
        q.add(start)
        while (q.isNotEmpty()) {
            // compute the size of the function.
            val s = q.remove()
            var pc = s
            var depth = 0
            val ints = mutableListOf<Int>()
            while (true) {
                var opint = buf.getIntAdj(pc)
                val opcode = opint and 0b1111111111
                val special = (opint ushr 14)
                val argCount = (opint ushr 10) and 0b1111
                val args = mutableListOf<Int>()
                pc += 4

                for (i in 1..argCount) {
                    args.add(buf.getIntAdj(pc))
                    pc += 4
                }

                if (opcode to special in STRING_OPS) {
                    val arg = STRING_OPS[opcode to special] ?: 0
                    if (args[arg] > 0x100000) {
                        strings.add(args[arg] to buf.getString(args[arg]))
                    }
                }

                if (!isRemix && opcode == 0x28 && special == 0 && engine == -1) {
                    engine = args[0]
                    if (allSubs) {
                        q.addAll(
                                LOCATIONS[engine].filter { it > 0x100000 })
                    }
                }

                if (!isRemix && (opcode == 0 || opcode == 4) && args[0] >= 0x56 && args[0] < 0x56 + LOCATIONS[engine].size) {
                    // macro/sub detected.
                    val location = LOCATIONS[engine][args[0] - 0x56]
                    if (!q.contains(location) && !result.any { it.first == location }) {
                        q.add(location)
                    }
                    args[0] = location
                    if (opcode == 0) {
                        opint = 2 or (2 shl 10)
                        args.removeAt(2)
                    } else {
                        opint = 6 or (1 shl 10)
                    }
                }

                if (opcode == 2 || opcode == 6) {
                    val location = args[0]
                    if (!q.contains(location) && !result.any { it.first == location }) {
                        q.add(location)
                    }
                }

                if (opcode == 1 && special == 1) {
                    val location = args[1]
                    if (!q.contains(location) && !result.any { it.first == location }) {
                        q.add(location)
                    }
                }

                if (opcode == 0x16 || opcode == 0x19) {
                    depth++
                }

                if (opcode == 0x18 || opcode == 0x1D) {
                    depth--
                }

                ints.add(opint)
                ints.addAll(args)

                if (opcode in 7..8 && depth == 0) {
                    break
                }
            }
            result.add(Pair(s, ints))
        }
        return result
    }

}