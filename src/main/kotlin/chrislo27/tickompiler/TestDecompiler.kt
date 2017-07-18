package chrislo27.tickompiler

import chrislo27.tickompiler.decompiler.Decompiler
import java.nio.ByteOrder

fun main(args: Array<String>) {

    val input: String = """
00 00 00 00 00 00 00 00 00 00 01 C0 00 00 08 02 00 00 00 1C 00 00 00 00 00 00 08 02 00 00 00 34 00 00 00 00 00 00 00 08 00 00 0C 00 00 00 00 56 00 00 00 00 00 00 07 D0 00 0C 00 0E 00 00 00 08 00 00 0C 00 00 00 00 57 00 00 00 00 00 00 07 D0 00 0C 00 0E 00 00 00 08
"""
    val strippedInput = input.replace("[ \t\n]".toRegex(), "")
    val byteArray = (0..strippedInput.length / 2 - 1).map {
        (Integer.parseUnsignedInt(strippedInput.substring(it * 2, it * 2 + 2), 16) and 0xFF).toByte()
    }.toByteArray()

    println(byteArray.size)

    val decompiler = Decompiler(byteArray, ByteOrder.LITTLE_ENDIAN, MegamixFunctions)

    val result = decompiler.decompile(addComments = true, useMetadata = true)
    println("Took ${result.first} ms\n${result.second}")

}
