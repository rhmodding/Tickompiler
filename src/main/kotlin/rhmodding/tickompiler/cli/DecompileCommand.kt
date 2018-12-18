package rhmodding.tickompiler.cli

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import rhmodding.tickompiler.DSFunctions
import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.util.getDirectories
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files


@CommandLine.Command(name = "decompile", aliases = ["d"], description = ["Decompile file(s) and output them to the file/directory specified.",
    "Files must be with the file extension .bin (little-endian)",
    "Files will be overwritten without warning.",
    "If the output is not specified, the file will be a .tickflow file with the same name."],
        mixinStandardHelpOptions = true)
class DecompileCommand : Runnable {

    @CommandLine.Option(names = ["-c"], description = ["Continue even with errors."])
    var continueWithErrors: Boolean = false

    @CommandLine.Option(names = ["-nc", "--no-comments"], description = ["Don't include comments."])
    var noComments: Boolean = false

    @CommandLine.Option(names = ["--bytecode"], description = ["Have a comment with the bytecode (overridden by --no-comments)."])
    var showBytecode: Boolean = false

    @CommandLine.Option(names = ["-nm", "--no-metadata"], description = ["No metadata (use when decompiling snippets instead of full files)."])
    var noMetadata: Boolean = false

    @CommandLine.Option(names = ["-m", "--megamix"], description = ["Decompile with Megamix functions. (default true)"])
    var megamixFunctions: Boolean = true

    @CommandLine.Option(names = ["--ds"], description = ["Decompile with RHDS functions (also disables Megamix-specific metadata)"])
    var dsFunctions: Boolean = false

    @CommandLine.Parameters(index = "0", arity = "1", description = ["Input file or directory."])
    lateinit var inputFile: File

    @CommandLine.Parameters(index = "1", arity = "0..1", description = ["Output file or directory."])
    var outputFile: File? = null

    override fun run() {
        val nanoStart = System.nanoTime()
        val dirs = getDirectories(inputFile, outputFile, { s -> s.endsWith(".bin") }, "tickflow")
        val functions = when {
            dsFunctions -> DSFunctions
            else -> MegamixFunctions
        }

        val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

        println("Decompiling ${dirs.input.size} file(s)")
        dirs.input.forEachIndexed { index, file ->
            coroutines += GlobalScope.async {
                val decompiler = Decompiler(Files.readAllBytes(file.toPath()),
                        ByteOrder.LITTLE_ENDIAN, functions)

                try {
                    println("Decompiling ${file.path}")
                    val result = decompiler.decompile(when {
                        noComments -> CommentType.NONE
                        showBytecode -> CommentType.BYTECODE
                        else -> CommentType.NORMAL
                    }, !noMetadata && functions == MegamixFunctions)

                    dirs.output[index].createNewFile()
                    val fos = FileOutputStream(dirs.output[index])
                    fos.write(result.second.toByteArray(Charset.forName("UTF-8")))
                    fos.close()

                    println("Decompiled ${file.path} -> ${result.first} ms")
                    return@async true
                } catch (e: RuntimeException) {
                    if (continueWithErrors) {
                        println("FAILED to decompile ${file.path}")
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

            println("""
+========================+
| DECOMPILATION COMPLETE |
+========================+
$successful / ${dirs.input.size} decompiled successfully in ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms
""")
        }
    }

}