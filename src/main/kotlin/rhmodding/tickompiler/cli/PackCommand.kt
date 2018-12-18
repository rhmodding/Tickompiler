package rhmodding.tickompiler.cli

import picocli.CommandLine
import rhmodding.tickompiler.gameputter.GamePutter
import rhmodding.tickompiler.util.getDirectories
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files

@CommandLine.Command(name = "pack", aliases = ["p"], description = ["Pack binary and tempo files from a specified directory into the output file, using the specified base file.",
    "The base file can be obtained from extraction.",
    "Files must be with the file extension .bin (little-endian) or .tempo",
    "The output file will be overwritten without warning.",
    "If the output is not specified, it will default to \"C00.bin\"."],
        mixinStandardHelpOptions = true)
class PackCommand : Runnable {

    @CommandLine.Parameters(index = "0", arity = "1", description = ["input directory"])
    lateinit var inputFile: File

    @CommandLine.Parameters(index = "1", arity = "1", description = ["base file"])
    lateinit var baseFile: File

    @CommandLine.Parameters(index = "2", arity = "0..1", description = ["output file"])
    var outputFile: File? = null

    override fun run() {
        val dirs = getDirectories(inputFile, baseFile, { it.endsWith(".bin") || it.endsWith(".tempo") }, "", true).input
        val base = Files.readAllBytes(baseFile.toPath())
        var index = base.size
        val baseBuffer = ByteBuffer.wrap(base).order(ByteOrder.LITTLE_ENDIAN)
        val out = ByteArrayOutputStream()
        val putter = GamePutter
        for (file in dirs) {
            println("Packing ${file.name}")
            val contents = Files.readAllBytes(file.toPath())
            val ints = if (file.path.endsWith(".bin")) {
                putter.putGame(baseBuffer, ByteBuffer.wrap(contents).order(ByteOrder.LITTLE_ENDIAN), index)
            } else {
                putter.putTempo(baseBuffer, contents.toString(Charset.forName("UTF-8")), index)
            }
            index += ints.size * 4
            val byteBuffer = ByteBuffer.allocate(ints.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            val intBuf = byteBuffer.asIntBuffer()
            intBuf.put(ints.toIntArray())
            val arr = ByteArray(ints.size * 4)
            byteBuffer[arr, 0, ints.size * 4]
            out.write(arr)
        }
        val file = outputFile ?: File("C00.bin")
        val fos = FileOutputStream(file)
        fos.write(baseBuffer.array())
        fos.write(out.toByteArray())
        fos.close()
    }

}