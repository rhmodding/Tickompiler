package rhmodding.tickompiler.gameextractor

import java.nio.ByteBuffer

const val TABLE_OFFSET = 0x52B498
const val TEMPO_TABLE = 0x53EF54
const val GATE_TABLE = 0x52E488

fun ByteBuffer.getIntAdj(index: Int): Int =
        this.getInt(index - 0x100000)

fun ByteBuffer.getUnicodeString(index: Int): String {
    var i: Int = index - 0x100000
    val str: StringBuilder = StringBuilder()
    while (true) {
        val char: Char = this.getChar(i)
        if (char == '\u0000') {
            return str.toString()
        }
        str.append(char)
        i += 2
    }
}

fun ByteBuffer.getASCIIString(index: Int): String {
    var i: Int = index - 0x100000
    val str: StringBuilder = StringBuilder()
    while (true) {
        val char: Char = this.get(i).toChar()
        if (char.toByte().toInt() == 0) {
            return str.toString()
        }
        str.append(char)
        i++
    }
}

fun ByteBuffer.getName(index: Int): String {
    val pointer: Int = this.getIntAdj(TABLE_OFFSET + 52 * index + 12)
    val str: String = this.getUnicodeString(pointer)
    return str.slice((str.lastIndexOf('_') + 1) until str.lastIndexOf('.'))
}

fun ByteBuffer.getStart(index: Int): Int =
        getIntAdj(TABLE_OFFSET + 52 * index + 4)

fun ByteBuffer.getGateStart(index: Int): Int =
        getIntAdj(GATE_TABLE + 36 * index + 4)

fun ByteBuffer.getGateName(index: Int): String {
    val pointer: Int = getIntAdj(GATE_TABLE + 36 * index + 16)
    val str: String = getASCIIString(pointer)
    return str.slice(str.indexOf('_') + 1 until str.lastIndexOf('_'))
}