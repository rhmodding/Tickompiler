package rhmodding.tickompiler.api

import rhmodding.tickompiler.gameputter.GamePutter
import rhmodding.tickompiler.util.getDirectories
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths


object PackCommand : Command("pack", "p") {

    override val commandInfo: CommandInfo =
            CommandInfo("<input dir> <base file> [output file]",
                        listOf("Pack binary and tempo files from a specified directory into the output file, using the specified base file.",
                               "The base file can be obtained from extraction.",
                               "Files must be with the file extension .bin (little-endian) or .tempo",
                               "The output file will be overwritten without warning.",
                               "If the output is not specified, it will default to \"C00.bin\"."),
                        listOf())

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                         output: PrintStream) {
        val dirs = getDirectories(flagsObj, args, { it.endsWith(".bin") || it.endsWith(".tempo") }, "", true).input
        val base = Files.readAllBytes(Paths.get(args[indexOfFirstArgument + 1]))
        var index = base.size
        val baseBuffer = ByteBuffer.wrap(base).order(ByteOrder.LITTLE_ENDIAN)
        val out = ByteArrayOutputStream()
        val putter = GamePutter
        for (file in dirs) {
            output.println("Packing ${file.name}")
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
        val file = File(when {
                            indexOfFirstArgument + 2 < args.size -> args[indexOfFirstArgument + 2]
                            else -> "C00.bin"
                        })
        val fos = FileOutputStream(file)
        fos.write(baseBuffer.array())
        fos.write(out.toByteArray())
        fos.close()
    }

}