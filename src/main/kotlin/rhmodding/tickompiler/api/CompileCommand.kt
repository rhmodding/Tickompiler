package rhmodding.tickompiler.api

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import rhmodding.tickompiler.DSFunctions
import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.compiler.Compiler
import rhmodding.tickompiler.util.getDirectories
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.ByteOrder


object CompileCommand : Command("compile", "c") {

    override val commandInfo: CommandInfo =
            CommandInfo("[flags] <input file or dir> [output file or dir]",
                        listOf("Compile file(s) and output them to the file/directory specified.",
                               "Files must be with the file extension .tickflow",
                               "Files will be overwritten without warning.",
                               "If the output is not specified, the file will be a (little-endian) .bin file with the same name."),
                        listOf(
                                FlagInfo(listOf("-c"), listOf("Continue even with errors")),
                                FlagInfo(listOf("-m"), listOf("Compile with Megamix functions (default)")),
                                FlagInfo(listOf("-ds"), listOf("Compile with RHDS functions"))
                              ))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                         output: PrintStream) {
        val nanoStart: Long = System.nanoTime()
        val dirs = getDirectories(flagsObj, args, { s -> s.endsWith(".tickflow") }, "bin")
        val functions = when {
            flags.contains("-m") -> MegamixFunctions
            flags.contains("-ds") -> DSFunctions
            else -> MegamixFunctions
        }

        output.println("Compiling ${dirs.input.size} file(s)")

        val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

        dirs.input.forEachIndexed { index, file ->
            coroutines += GlobalScope.async {
                val compiler = Compiler(file, functions)

                try {
                    output.println("Compiling ${file.path}")
                    val result = compiler.compile(ByteOrder.LITTLE_ENDIAN)

                    if (result.success) {
                        dirs.output[index].createNewFile()
                        val fos = FileOutputStream(dirs.output[index])
                        fos.write(result.data.array())
                        fos.close()

                        output.println("Compiled ${file.path} -> ${result.timeMs} ms")
                        return@async true
                    }
                } catch (e: Exception) {
                    if (flags.contains("-c")) {
                        output.println("FAILED to compile ${file.path}")
                        e.printStackTrace()
                    } else {
                        throw e
                    }
                }

                return@async false
            }
        }

        runBlocking {
            val successful = coroutines
                    .map { it.await() }
                    .count { it }

            output.println("""
+======================+
| COMPILATION COMPLETE |
+======================+
$successful / ${dirs.input.size} compiled successfully in ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms
""")
        }
    }
}