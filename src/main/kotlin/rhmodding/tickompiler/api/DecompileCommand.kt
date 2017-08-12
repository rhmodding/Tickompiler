package rhmodding.tickompiler.api

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import rhmodding.tickompiler.DSFunctions
import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.util.getDirectories
import java.io.FileOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files


object DecompileCommand : Command("decompile", "d") {

    override val commandInfo: CommandInfo =
            CommandInfo("[flags] <input file or dir> [output file or dir]",
                        listOf("Decompile file(s) and output them to the file/directory specified.",
                               "Files must be with the file extension .bin (little-endian)",
                               "Files will be overwritten without warning.",
                               "If the output is not specified, the file will be a .tickflow file with the same name."),
                        listOf(
                                FlagInfo(listOf("-c"), listOf("Continue even with errors")),
                                FlagInfo(listOf("-nc"), listOf("No comments")),
                                FlagInfo(listOf("-bytecode"), listOf("Have a comment with the bytecode - no comments will override this")),
                                FlagInfo(listOf("-nm"), listOf("No metadata (use when decompiling snippets instead of full files)")),
                                FlagInfo(listOf("-m"), listOf("Compile with Megamix functions (default)")),
                                FlagInfo(listOf("-ds"), listOf("Compile with RHDS functions (also disables Megamix-specific metadata)"))
                              ))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                         output: PrintStream) {
        val nanoStart = System.nanoTime()
        val dirs = getDirectories(flagsObj, args, { s -> s.endsWith(".bin") }, "tickflow")
        val functions = when {
            flags.contains("-m") -> MegamixFunctions
            flags.contains("-ds") -> DSFunctions
            else -> MegamixFunctions
        }

        val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

        output.println("Decompiling ${dirs.input.size} file(s)")
        dirs.input.forEachIndexed { index, file ->
            coroutines += async(CommonPool) {
                val decompiler = Decompiler(Files.readAllBytes(file.toPath()),
                                            ByteOrder.BIG_ENDIAN, functions)

                try {
                    output.println("Decompiling ${file.path}")
                    val result = decompiler.decompile(if (flags.contains(
                            "-nc")) CommentType.NONE else if ("-bytecode" in flags) CommentType.BYTECODE else CommentType.NORMAL,
                                                      !flags.contains("-nm") && functions == MegamixFunctions)

                    dirs.output[index].createNewFile()
                    val fos = FileOutputStream(dirs.output[index])
                    fos.write(result.second.toByteArray(Charset.forName("UTF-8")))
                    fos.close()

                    output.println("Decompiled ${file.path} -> ${result.first} ms")
                    return@async true
                } catch (e: RuntimeException) {
                    if (flags.contains("-c")) {
                        output.println("FAILED to decompile ${file.path}")
                        e.printStackTrace()
                    } else {
                        throw e
                    }
                }

                return@async true
            }
        }

        runBlocking {
            val successful = coroutines
                    .map { it.await() }
                    .count { it }

            output.println("""
+========================+
| DECOMPILATION COMPLETE |
+========================+
$successful / ${dirs.input.size} decompiled successfully in ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms
""")
        }
    }

}