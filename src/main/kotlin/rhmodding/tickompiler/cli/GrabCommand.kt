package rhmodding.tickompiler.cli

import picocli.CommandLine
import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.gameextractor.GameExtractor
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files

@CommandLine.Command(name = "grab", aliases = ["g"], description = ["Extract tickflow code from a specified location in the given code.bin file and output to the specified file.",
    "File must have the extension .bin.",
    "File will be overwritten without warning.",
    "Location is to be specified in hexadecimal without 0x prefix.",
    "If the output is not specified, the file will have the given location as name."],
        mixinStandardHelpOptions = true)
class GrabCommand : Runnable {

    @CommandLine.Option(names = ["-d", "--decompile"], description = ["Immediately decompile into a .tickflow file with the same name as the output."])
    var decompileImmediately: Boolean = false

    @CommandLine.Parameters(index = "0", arity = "1", description = ["code file"])
    lateinit var codeFile: File

    @CommandLine.Parameters(index = "1", arity = "1", description = ["location"])
    lateinit var location: String

    @CommandLine.Parameters(index = "2", arity = "0..1", description = ["output file"])
    var outputFile: File? = null

    override fun run() {
        val codebin = codeFile
        val codeBuffer = ByteBuffer.wrap(Files.readAllBytes(codebin.toPath())).order(ByteOrder.LITTLE_ENDIAN)
        val location = location.toInt(16)
        val o = outputFile ?: File("$location.bin")
        val result = GameExtractor(false).extractArbitrary(codeBuffer, location)
        val byteBuffer = ByteBuffer.allocate(result.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        val intBuf = byteBuffer.asIntBuffer()
        intBuf.put(result.toIntArray())
        val arr = ByteArray(result.size * 4)
        byteBuffer[arr, 0, result.size * 4]
        val fos = FileOutputStream(o)
        fos.write(arr)
        fos.close()
        if (decompileImmediately) {
            val decompiler = Decompiler(arr, ByteOrder.LITTLE_ENDIAN, MegamixFunctions)
            println("Decompiling ${o.nameWithoutExtension}")
            val r = decompiler.decompile(CommentType.NORMAL, true)
            val f = FileOutputStream(File(o.absolutePath.dropLastWhile { it != File.separatorChar } + o.nameWithoutExtension + ".tickflow"))
            f.write(r.second.toByteArray(Charset.forName("UTF-8")))
            f.close()
            println("Decompiled ${o.nameWithoutExtension} -> ${r.first} ms")
        }
    }

}