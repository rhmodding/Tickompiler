package rhmodding.tickompiler.api

import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.gameextractor.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files


object ExtractCommand : Command("extract", "e") {

    override val commandInfo: CommandInfo =
            CommandInfo("[flags] <code file> [output dir]",
                        listOf("Extract binary files from a decrypted code.bin file and output them to the directory specified.",
                               "File must be with the file's extension .bin (little-endian)",
                               "Files will be overwritten without warning.",
                               "If the output is not specified, the directory will have the same name as the file.",
                               "A base.bin file will also be created in the output directory. This is a base C00.bin file."),
                        listOf(
                                FlagInfo(listOf("-a"),
                                         listOf("Extract all subroutines of game engines, as opposed to only ones used by the games.",
                                                "Note that this potentially includes sequels and prequels to the game.")),
                                FlagInfo(listOf("-d"),
                                         listOf("Immediately decompile all extracted games, with enhanced features such as meaningful marker names.",
                                                "Will be extracted into a \"decompiled\" directory in the output directory.")),
                                FlagInfo(listOf("-t"),
                                         listOf("Extract tempo files. These will be written as .tempo files in a \"tempo\" folder in the output directory."))
                              ))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                         output: PrintStream) {
        if (indexOfFirstArgument == -1) {
            throw IllegalArgumentException("A code file needs to be specified!")
        }
        val codebin = File(args[indexOfFirstArgument])
        val codeBuffer = ByteBuffer.wrap(Files.readAllBytes(codebin.toPath())).order(ByteOrder.LITTLE_ENDIAN)
        val folder = File(when {
                              indexOfFirstArgument + 1 < args.size -> args[indexOfFirstArgument + 1]
                              else -> codebin.nameWithoutExtension
                          })
        folder.mkdirs()
        val decompiledFolder = File(folder, "decompiled")
        if (flags.contains("-d")) {
            decompiledFolder.mkdirs()
        }
        for (i in 0 until 104) {
            output.println("Extracting ${codeBuffer.getName(i)}")
            val result = GameExtractor(
                    flags.contains("-a")).extractGame(codeBuffer, i)
            val ints = result.second
            val byteBuffer = ByteBuffer.allocate(ints.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            val intBuf = byteBuffer.asIntBuffer()
            intBuf.put(ints.toIntArray())
            val arr = ByteArray(ints.size * 4)
            byteBuffer[arr, 0, ints.size * 4]
            val fos = FileOutputStream(File(folder, codeBuffer.getName(i) + ".bin"))
            fos.write(arr)
            fos.close()
            if (flags.contains("-d")) {
                val decompiler = Decompiler(arr, ByteOrder.BIG_ENDIAN, MegamixFunctions)
                output.println("Decompiling ${codeBuffer.getName(i)}")
                val r = decompiler.decompile(CommentType.NORMAL, true, "    ", result.first)
                val f = FileOutputStream(File(decompiledFolder, codeBuffer.getName(i) + ".tickflow"))
                f.write(r.second.toByteArray(Charset.forName("UTF-8")))
                f.close()
                output.println("Decompiled ${codeBuffer.getName(i)} -> ${r.first} ms")
            }
        }
        for (i in 0 until 16) {
            println("Extracting ${codeBuffer.getGateName(i)}")
            val result = GameExtractor(flags.contains("-a")).extractGateGame(codeBuffer, i)
            val ints = result.second
            val byteBuffer = ByteBuffer.allocate(ints.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            val intBuf = byteBuffer.asIntBuffer()
            intBuf.put(ints.toIntArray())
            val arr = ByteArray(ints.size * 4)
            byteBuffer[arr, 0, ints.size * 4]
            val fos = FileOutputStream(File(folder, codeBuffer.getGateName(i) + ".bin"))
            fos.write(arr)
            fos.close()
            if (flags.contains("-d")) {
                val decompiler = Decompiler(arr, ByteOrder.BIG_ENDIAN, MegamixFunctions)
                output.println("Decompiling ${codeBuffer.getGateName(i)}")
                val r = decompiler.decompile(CommentType.NORMAL, true, "    ", result.first)
                val f = FileOutputStream(File(decompiledFolder, codeBuffer.getGateName(i) + ".tickflow"))
                f.write(r.second.toByteArray(Charset.forName("UTF-8")))
                f.close()
                output.println("Decompiled ${codeBuffer.getGateName(i)} -> ${r.first} ms")
            }
        }
        if (flags.contains("-t")) {
            val tempoFolder = File(folder, "tempo")
            tempoFolder.mkdirs()
            for (i in 0 until 0x1DD) {
                val tempoPair = GameExtractor.extractTempo(codeBuffer, i)
                val f = FileOutputStream(File(tempoFolder, tempoPair.first + ".tempo"))
                f.write(tempoPair.second.toByteArray(Charset.forName("UTF-8")))
                f.close()
                output.println("Extracted tempo file ${tempoPair.first}")
            }
        }
        val tableList = ByteArray(104 * 53)
        codeBuffer.position(TABLE_OFFSET - 0x100000)
        codeBuffer.get(tableList, 0, 104 * 53)
        val tempoList = ByteArray(16 * 0x1DD)
        codeBuffer.position(TEMPO_TABLE - 0x100000)
        codeBuffer.get(tempoList, 0, 16 * 0x1DD)
        val gateList = ByteArray(16 * 36 + 16 * 4)
        codeBuffer.position(GATE_TABLE - 0x100000)
        codeBuffer.get(gateList, 0, 16 * 36 + 16 * 4)
        val fos = FileOutputStream(File(folder, "base.bin"))
        fos.write(tableList)
        fos.write(tempoList)
        fos.write(gateList)
        fos.close()
    }

    override fun onThrowable(throwable: Throwable, output: PrintStream) {
        if (throwable is IndexOutOfBoundsException) {
            output.println("Did you verify that your code.bin is decrypted?\n")
        }

        super.onThrowable(throwable, output)
    }
}