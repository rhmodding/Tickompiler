package chrislo27.tickompiler.gameputter

import java.nio.ByteBuffer

class GamePutter {

	fun putGame(base: ByteBuffer, gameContents: ByteBuffer, s: Int): List<Int> {
		val tableIndex = gameContents.getInt(0)
		val start = gameContents.getInt(4)
		val assets = gameContents.getInt(8)

		base.putInt(52*tableIndex + 4, 0xC000000 + start + s)
		base.putInt(52*tableIndex + 8, 0xC000000 + assets + s)

		val result = mutableListOf<Int>()
		gameContents.position(12)
		while (gameContents.hasRemaining()) {
			val opint = gameContents.int
			result.add(opint)
			val opcode = opint and 0b1111111111
			val special = (opint ushr 14)
			val argCount = (opint ushr 10) and 0b1111
			val args: MutableList<Int> = (1..argCount).map {
				gameContents.int
			}.toMutableList()
			if ((opcode == 2 || opcode == 6) && special == 0) {
				if (args[0] < 0x100000) {
					args[0] += 0xC000000 + s
				}
			}
			if (opcode == 1 && special == 1) {
				if (args[1] < 0x100000) {
					args[1] += 0xC000000 + s
				}
			}
			result.addAll(args)
		}
		return result
	}

	fun putTempo(base: ByteBuffer, tempoFile: String, s: Int): List<Int> {
		val tempo = tempoFile.split(Regex("\\r?\\n")).filter { it.isNotBlank() }.map { it.split(" ") }
		val ids = tempo[0].map { it.toInt(16) }
		val result = mutableListOf<Int>()
		tempo.drop(1).forEachIndexed { index, list ->
			val bpm = list[0].toFloat()
			val beats = list[1].toFloat()
			val time = 60*beats/bpm
			val timeInt = Math.round(time * 32000)
			result.add(java.lang.Float.floatToIntBits(beats))
			result.add(timeInt)
			if (index == tempo.size - 2) {
				result.add(0x8000)
			} else {
				result.add(0)
			}
		}
		for (i in 0 until 0x1DD) {
			val one = base.getInt(16*i + 0x1588)
			val two = base.getInt(16*i + 0x1588 + 4)
			if (one in ids || two in ids) {
				base.putInt(16*i + 0x1588 + 12, 0xC000000 + s)
			}
		}
		return result
	}
}