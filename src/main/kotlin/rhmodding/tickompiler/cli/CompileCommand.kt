package rhmodding.tickompiler.cli

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import rhmodding.tickompiler.DSFunctions
import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.compiler.Compiler
import rhmodding.tickompiler.util.getDirectories
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteOrder


@CommandLine.Command(name = "compile", aliases = ["c"], description = ["Compile file(s) and output them to the file/directory specified.",
    "Files must be with the file extension .tickflow",
    "Files will be overwritten without warning.",
    "If the output is not specified, the file will be a (little-endian) .bin file with the same name."],
        mixinStandardHelpOptions = true)
class CompileCommand : Runnable {

    @CommandLine.Option(names = ["-c"], description = ["Continue even with errors."])
    var continueWithErrors: Boolean = false

    @CommandLine.Option(names = ["-m", "--megamix"], description = ["Compile with Megamix functions. (default true)"])
    var megamixFunctions: Boolean = true

    @CommandLine.Option(names = ["--ds"], description = ["Compile with RHDS functions."])
    var dsFunctions: Boolean = false

    @CommandLine.Parameters(index = "0", arity = "1", description = ["Input file or directory."])
    lateinit var inputFile: File

    @CommandLine.Parameters(index = "1", arity = "0..1", description = ["Output file or directory."])
    var outputFile: File? = null

    override fun run() {
        val nanoStart: Long = System.nanoTime()
        val dirs = getDirectories(inputFile, outputFile, { s -> s.endsWith(".tickflow") }, "bin")
        val functions = when {
            dsFunctions -> DSFunctions
            else -> MegamixFunctions
        }

        println("Compiling ${dirs.input.size} file(s)")

        val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

        dirs.input.forEachIndexed { index, file ->
            coroutines += GlobalScope.async {
                val compiler = Compiler(file, functions)

                try {
                    println("Compiling ${file.path}")
                    val result = compiler.compile(ByteOrder.LITTLE_ENDIAN)

                    if (result.success) {
                        dirs.output[index].createNewFile()
                        val fos = FileOutputStream(dirs.output[index])
                        fos.write(result.data.array())
                        fos.close()

                        println("Compiled ${file.path} -> ${result.timeMs} ms")
                        return@async true
                    }
                } catch (e: Exception) {
                    if (continueWithErrors) {
                        println("FAILED to compile ${file.path}")
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

            println("""
+======================+
| COMPILATION COMPLETE |
+======================+
$successful / ${dirs.input.size} compiled successfully in ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms
""")
        }
    }

}
