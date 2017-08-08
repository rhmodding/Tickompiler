package rhmodding.tickompiler.test

import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import java.nio.ByteOrder

fun main(args: Array<String>) {

    val input: String = """
00 00 00 00 00 00 00 00 C0 01 00 00 FF FF FF FF 01 00 00 00 00 00 00 00 02 08 00 00 34 00 00 00 00 00 00 00 FF FF FF FF 01 00 00 00 00 00 00 00 02 08 00 00 4C 00 00 00 00 00 00 00 08 00 00 00 00 0C 00 00 56 00 00 00 00 00 00 00 D0 07 00 00 0E 00 0C 00 08 00 00 00 FF FF FF FF 01 00 00 00 01 00 00 00 00 0C 00 00 70 00 00 00 00 00 00 00 D0 07 00 00 0E 00 0C 00 08 00 00 00 FE FF FF FF 30 78 35 37 5C 22 00 00
"""
    val strippedInput = input.replace("[ \t\n]".toRegex(), "")
    val byteArray = (0..strippedInput.length / 2 - 1).map {
        (Integer.parseUnsignedInt(strippedInput.substring(it * 2, it * 2 + 2), 16) and 0xFF).toByte()
    }.toByteArray()

    println(byteArray.size)

    val decompiler = Decompiler(byteArray, ByteOrder.LITTLE_ENDIAN,
                                MegamixFunctions)

    val result = decompiler.decompile(addComments = CommentType.NORMAL, useMetadata = true)
    println("Took ${result.first} ms\n${result.second}")

}
