package rhmodding.tickompiler.gameputter

import java.nio.ByteBuffer
import kotlin.math.roundToLong

object GamePutter {

	fun putGame(base: ByteBuffer, gameContents: ByteBuffer, s: Int): List<Int> {
		val tableIndex = gameContents.getInt(0)
		val start = gameContents.getInt(4)
		val assets = gameContents.getInt(8)
		if (tableIndex >= 0x100) {
			base.putInt(0x3358 + 36*(tableIndex-0x100) + 4, 0xC000000 + start + s)
			base.putInt(0x3358 + 36*(tableIndex-0x100) + 8, 0xC000000 + assets + s)
		} else {
			base.putInt(52 * tableIndex + 4, 0xC000000 + start + s)
			base.putInt(52 * tableIndex + 8, 0xC000000 + assets + s)
		}

		val result = mutableListOf<Int>()
		gameContents.position(12)
		while (gameContents.hasRemaining()) {
			var opint = gameContents.int
			if (opint == -2) {
				break
			}
			val adjArgs = mutableListOf<Int>()
			if (opint == -1) {
				val amount = gameContents.int
				for (i in 1..amount) {
					val ann = gameContents.int
					val anncode = ann and 0xFF
					val annArg = ann ushr 8
					if (anncode == 0 || anncode == 1 || anncode == 2) {
						adjArgs.add(annArg)
					}
				}
				opint = gameContents.int
			}
			result.add(opint)
			val argCount = (opint ushr 10) and 0b1111
			val args: MutableList<Int> = (0 until argCount).map {
				val arg = gameContents.int
				if (it in adjArgs) {
					arg + 0xC000000 + s
				} else {
					arg
				}
			}.toMutableList()
			result.addAll(args)
		}
		while (gameContents.hasRemaining()) {
			result.add(gameContents.int)
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
			val timeInt = (time * 32000).roundToLong()
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