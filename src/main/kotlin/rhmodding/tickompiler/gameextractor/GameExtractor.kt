package rhmodding.tickompiler.gameextractor

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong


class GameExtractor(val allSubs: Boolean) {

    private var engine = -1
    private var isRemix = false
    private var ustrings = mutableListOf<Pair<Int, String>>()
	private var astrings = mutableListOf<Pair<Int, String>>()

    private val USTRING_OPS = mutableMapOf(
            0x31 to 0 to arrayOf(1),
            0x35 to 0 to arrayOf(1),
            0x39 to 0 to arrayOf(1),
            0x3E to 0 to arrayOf(1),
            0x5D to 0 to arrayOf(1),
            0x5D to 2 to arrayOf(0),
			0x61 to 2 to arrayOf(0)
	)

	private val ASTRING_OPS = mutableMapOf(
            0x3B to 0 to arrayOf(2),
			0x67 to 1 to arrayOf(1),
            0x93 to 0 to arrayOf(2, 3),
            0x94 to 0 to arrayOf(1, 2, 3),
            0x95 to 0 to arrayOf(1),
            0xB0 to 4 to arrayOf(1),
            0xB0 to 5 to arrayOf(1),
            0xB0 to 6 to arrayOf(1),
            0x66 to 0 to arrayOf(1),
            0x65 to 1 to arrayOf(1),
            0x68 to 1 to arrayOf(1),
            0xAF to 2 to arrayOf(2),
            0xB5 to 0 to arrayOf(0)
	)

    companion object {
        val LOCATIONS: List<List<Int>> by lazy {
            Gson().fromJson<List<List<Int>>>(
                    GameExtractor::class.java.getResource("/locations.json").readText())
        }

        private const val TEMPO_TABLE = 0x53EF54
        private const val DECIMALS: Int = 3

        private fun correctlyRoundDouble(value: Double, places: Int): String {
            if (places < 0)
                error("Places $places cannot be negative")
            if (places == 0)
                return "$value"
            val long: Long = (value * 10.0.pow(places.toDouble())).roundToLong()
            val longString = long.toString()

            val str = longString.substring(0, longString.length - places) + "." + longString.substring(longString.length - places).trimEnd('0')

            return if (str.endsWith('.')) "${str}0" else str
        }

        fun extractTempo(buffer: ByteBuffer, index: Int): Pair<String, String> {
            val ids = mutableListOf(buffer.getIntAdj(TEMPO_TABLE + 16 * index),
                                    buffer.getIntAdj(TEMPO_TABLE + 16 * index + 4))
                    .filter { it != -1 }
            val name = (if (ids[0] != -1) ids[0] else ids[1]).toString(16)
            var s: String = ids.joinToString(" ") { it.toString(16) } + "\n"
            var addr = buffer.getIntAdj(TEMPO_TABLE + 16 * index + 12)
            while (true) {
                val beats = buffer.getFloat(addr - 0x100000)
                val seconds = buffer.getInt(addr - 0x100000 + 4) / 32000.0 // will not work with unsigned but not important
                val bpm = 60 * beats / seconds
                s += "${correctlyRoundDouble(bpm, DECIMALS)} ${correctlyRoundDouble(beats.toDouble(), DECIMALS)}\n"
                if (buffer.getIntAdj(addr + 8) != 0)
                    break
                addr += 12
            }
            return name.toUpperCase(Locale.ROOT) to s.toUpperCase(Locale.ROOT)
        }
    }

    fun unicodeStringToInts(str: String): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0
        while (i <= str.length) {
            var int = 0
            if (i < str.length)
                int += str[i].toByte().toInt() shl 0
            if (i + 1 < str.length)
                int += str[i + 1].toByte().toInt() shl 16
            i += 2
            result.add(int)
        }
        return result
    }

	fun stringToInts(str: String): List<Int> {
		val result = mutableListOf<Int>()
		var i = 0
		while (i <= str.length) {
			var int = 0
			if (i < str.length)
				int += str[i].toByte().toInt() shl 0
			if (i + 1 < str.length)
				int += str[i + 1].toByte().toInt() shl 8
			if (i + 2 < str.length)
				int += str[i + 2].toByte().toInt() shl 16
			if (i + 3 < str.length)
				int += str[i + 3].toByte().toInt() shl 24
			i += 4
			result.add(int)
		}
		return result
	}

    fun extractGateGame(buffer: ByteBuffer, index: Int): Pair<Map<Int, Int>, List<Int>> {
        val start = buffer.getGateStart(index)
        engine = -1
        val funcs = firstPass(buffer, start, buffer.getIntAdj(GATE_TABLE + 36 * index + 8))
        val sorted = listOf(funcs[0]) + funcs.drop(1).sortedBy { it.first }
        val returnMap = mutableMapOf<Int, Int>()
        val map = mutableMapOf<Int, Int>()
        var i = 0
        for ((first, second) in sorted) {
            map[first] = i
            if (!isRemix && first in LOCATIONS[engine]) {
                returnMap[i] = LOCATIONS[engine].indexOf(first) + 0x56
            }
            i += second.size * 4
        }
        for ((first, second) in ustrings) {
            map[first] = i
            i += unicodeStringToInts(second).size * 4
        }
        for ((first, second) in astrings) {
            map[first] = i
            i += stringToInts(second).size * 4
        }
        val meta = mutableListOf<Int>()
        meta.add(index + 0x100)
        meta.add(0)
        meta.add(map[buffer.getIntAdj(GATE_TABLE + 36 * index + 8)] ?: 0)
        return returnMap to (meta + secondPass(sorted.map { it.second }, map))
    }

    fun extractGame(buffer: ByteBuffer, index: Int): Pair<Map<Int, Int>, List<Int>> {
        val start = buffer.getStart(index)
        engine = -1
        val funcs = firstPass(buffer, start)
        val sorted = listOf(funcs[0]) + funcs.drop(1).sortedBy { it.first }
        val returnMap = mutableMapOf<Int, Int>()
        val map = mutableMapOf<Int, Int>()
        var i = 0
        for ((first, second) in sorted) {
            map[first] = i
            if (!isRemix && first in LOCATIONS[engine]) {
                returnMap[i] = LOCATIONS[engine].indexOf(first) + 0x56
            }
            i += second.size * 4
        }
        for ((first, second) in ustrings) {
            map[first] = i
            i += unicodeStringToInts(second).size * 4
        }
		for ((first, second) in astrings) {
			map[first] = i
			i += stringToInts(second).size * 4
		}
        val meta = mutableListOf<Int>()
        meta.add(index)
        meta.add(0)
        meta.add(map[buffer.getIntAdj(TABLE_OFFSET + 52 * index + 8)] ?: 0)
        return returnMap to (meta + secondPass(sorted.map { it.second }, map))
    }

    fun extractArbitrary(buffer: ByteBuffer, index: Int): List<Int> {
        isRemix = true
        engine = -1
        val funcs = firstPass(buffer, index, nongame=true)
        val sorted = listOf(funcs[0]) + funcs.drop(1).sortedBy {it.first}
        val map = mutableMapOf<Int, Int>()
        var i = 0
        for ((first, second) in sorted) {
            map[first] = i
            i += second.size * 4
        }
        for ((first, second) in ustrings) {
            map[first] = i
            i += unicodeStringToInts(second).size * 4
        }
        for ((first, second) in astrings) {
            map[first] = i
            i += stringToInts(second).size * 4
        }
        val meta = mutableListOf<Int>()
        meta.add(-1)
        meta.add(0)
        meta.add(-1)
        return meta + secondPass(sorted.map {it.second}, map)
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
                val args = l.slice(i + 1..i + argCount).toMutableList()
                val annotations = mutableListOf<Int>()
                i += argCount + 1
                if (opcode == 2 || opcode == 6) {
                    annotations.add(0)
                    args[0] = map[args[0]] ?: 0
                }
                if (opcode == 3 && special == 2) {
                    annotations.add(0)
                    args[0] = map[args[0]] ?: 0
                }
                if (opcode == 1 && special == 1) {
                    annotations.add(0x100)
                    args[1] = map[args[1]] ?: 0
                }
                if (opcode to special in USTRING_OPS) {
                    val n = USTRING_OPS[opcode to special] ?: arrayOf()
                    for (arg in n) {
                        annotations.add(1 or (arg shl 8))
                        args[arg] = map[args[arg]] ?: args[arg]
                    }
                }
				if (opcode to special in ASTRING_OPS) {
					val n = ASTRING_OPS[opcode to special] ?: arrayOf()
                    for (arg in n) {
                        annotations.add(2 or (arg shl 8))
                        args[arg] = map[args[arg]] ?: args[arg]
                    }
				}
                if (annotations.size > 0) {
                    result.add(-1)
                    result.add(annotations.size)
                    result.addAll(annotations)
                }
                result.add(opint)
                result.addAll(args)
            }
        }
        result.add(-2)
        result.addAll(ustrings.map { unicodeStringToInts(it.second) }.flatten())
		result.addAll(astrings.map { stringToInts(it.second) }.flatten())
        return result
    }

    fun firstPass(buf: ByteBuffer, start: Int, assets: Int? = null, nongame: Boolean = false): List<Pair<Int, List<Int>>> {
        val result = mutableListOf<Pair<Int, List<Int>>>()
        ustrings = mutableListOf()

        isRemix = buf.getIntAdj(start) and 0b1111111111 == 1 || nongame
        val q = ArrayDeque<Int>()
        q.add(start)
        if (assets != null) {
            q.add(assets)
        }
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

                if (opcode to special in USTRING_OPS) {
                    val n = USTRING_OPS[opcode to special] ?: arrayOf()
                    n
                            .filter { args[it] > 0x100000 }
                            .forEach { ustrings.add(args[it] to buf.getUnicodeString(args[it])) }
                }

				if (opcode to special in ASTRING_OPS) {
					val n = ASTRING_OPS[opcode to special] ?: arrayOf()
                    n
                            .filter { args[it] > 0x100000 }
                            .forEach { astrings.add(args[it] to buf.getASCIIString(args[it])) }
				}

                if (!isRemix && opcode == 0x28 && special == 0 && engine == -1) {
                    engine = args[0]
                    if (allSubs) {
                        q.addAll(
                                LOCATIONS[engine].filter { it > 0x100000 })
                    }
                }

                if (!isRemix && (opcode == 0 || opcode == 4 || (opcode == 3 && special == 3)) && args[0] >= 0x56 && args[0] < 0x56 + LOCATIONS[engine].size) {
                    // macro/sub detected.
                    val location = LOCATIONS[engine][args[0] - 0x56]
                    if (!q.contains(location) && !result.any { it.first == location }) {
                        q.add(location)
                    }
                    args[0] = location
                    if (opcode == 0) {
                        opint = 2 or (2 shl 10)
                        args.removeAt(2)
                    } else if (opcode == 4) {
                        opint = 6 or (1 shl 10)
                    } else {
                        opint = 3 or (1 shl 10) or (2 shl 14)
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