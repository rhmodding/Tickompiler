package rhmodding.tickompiler.api

import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.gameextractor.GameExtractor
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files

object GrabCommand: Command("grab", "g") {

	override val commandInfo: CommandInfo = CommandInfo(
			"[flags] <code file> <location> [output file]",
			listOf("Extract tickflow code from a specified location in the given code.bin file and output to the specified file.",
					"File must have the extension .bin.",
					"File will be overwritten without warning.",
					"Location is to be specified in hexadecimal without 0x prefix.",
					"If the output is not specified, the file will have the given location as name."),
			listOf(
					FlagInfo(listOf("-d"),
							listOf("Immediately decompile into a .tickflow file with the same name as the output."))
			)
	)

	override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int, output: PrintStream) {
		if (indexOfFirstArgument == -1) {
			throw IllegalArgumentException("A code file needs to be specified!")
		}
		if (args.size - indexOfFirstArgument <= 1) {
			throw IllegalArgumentException("A location needs to be specified!")
		}
		val codebin = File(args[indexOfFirstArgument])
		val codeBuffer = ByteBuffer.wrap(Files.readAllBytes(codebin.toPath())).order(ByteOrder.LITTLE_ENDIAN)
		val location = args[indexOfFirstArgument + 1].toInt(16)
		val o = File(when {
			indexOfFirstArgument + 2 < args.size -> args[indexOfFirstArgument + 2]
			else -> args[indexOfFirstArgument+1] + ".bin"
		})
		val result = GameExtractor(false).extractArbitrary(codeBuffer, location)
		val byteBuffer = ByteBuffer.allocate(result.size * 4).order(ByteOrder.LITTLE_ENDIAN)
		val intBuf = byteBuffer.asIntBuffer()
		intBuf.put(result.toIntArray())
		val arr = ByteArray(result.size * 4)
		byteBuffer[arr, 0, result.size * 4]
		val fos = FileOutputStream(o)
		fos.write(arr)
		fos.close()
		if (flags.contains("-d")) {
			val decompiler = Decompiler(arr, ByteOrder.LITTLE_ENDIAN, MegamixFunctions)
			output.println("Decompiling ${o.nameWithoutExtension}")
			val r = decompiler.decompile(CommentType.NORMAL, true)
			val f = FileOutputStream(File(o.absolutePath.dropLastWhile { it != File.separatorChar } + o.nameWithoutExtension + ".tickflow"))
			f.write(r.second.toByteArray(Charset.forName("UTF-8")))
			f.close()
			output.println("Decompiled ${o.nameWithoutExtension} -> ${r.first} ms")
		}
	}

}