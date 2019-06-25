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


@CommandLine.Command(name = "compile", aliases = ["c"], description = ["Compile tickflow file(s) and output them as a binary to the file/directory specified.",
    "Files must be with the file extension .tickflow",
    "Files will be overwritten without warning.",
    "Use the --objectify/-o optional parameter to create a tkflwobj file, which contains the compiled data along with the tempo files required.",
    "If the output is not specified, the file will be a (little-endian) .bin file with the same name.",
    "If the output is not specified AND --objectify was used, the output file MUST be specified!"],
        mixinStandardHelpOptions = true)
class CompileCommand : Runnable {

    @CommandLine.Option(names = ["-c"], description = ["Continue even with errors."])
    var continueWithErrors: Boolean = false

    @CommandLine.Option(names = ["-m", "--megamix"], description = ["Compile with Megamix functions. (default true)"])
    var megamixFunctions: Boolean = true

    @CommandLine.Option(names = ["--ds"], description = ["Compile with RHDS functions."])
    var dsFunctions: Boolean = false

    @CommandLine.Option(names = ["-o", "--objectify"], paramLabel = "tempo files directory",
            description = ["Compile as a tkflwobj file. Provide the directory where required .tempo files are located."])
    var objectify: File? = null

    @CommandLine.Parameters(index = "0", arity = "1", description = ["Input file or directory."])
    lateinit var inputFile: File

    @CommandLine.Parameters(index = "1", arity = "0..1", description = ["Output file or directory. If --objectify is used, this is NOT optional and must be a file."])
    var outputFile: File? = null

    override fun run() {
        val nanoStart: Long = System.nanoTime()
        val tempoLoc = objectify
        if (tempoLoc != null && !tempoLoc.isDirectory) {
            throw IllegalArgumentException("--objectify was used but the path given was not a directory: ${tempoLoc.path}")
        } else if (tempoLoc != null) {
            if (outputFile == null) {
                throw IllegalArgumentException("--objectify was used but the output file was not specified or is not a file. It must be specified and must be a file.")
            }
            outputFile!!.createNewFile()
        }
        val objectifying = tempoLoc != null
        val dirs = getDirectories(inputFile, outputFile, { s -> s.endsWith(".tickflow") }, if (objectifying) "tkflwobj" else "bin")
        val functions = when {
            dsFunctions -> DSFunctions
            megamixFunctions -> MegamixFunctions
            else -> MegamixFunctions
        }
        val tempoFiles: List<File> = if (tempoLoc != null) tempoLoc.listFiles { f -> f.name.endsWith(".tempo") }!!.toList() else listOf()

        println("Compiling ${dirs.input.size} file(s)${if (objectifying) " with ${tempoFiles.size} tempo files" else ""} ")

        val outputBinFiles: List<Pair<File, File>> = if (!objectifying) {
            dirs.input.mapIndexed { i, f -> f to dirs.output[i] }
        } else {
            dirs.input.map { it to File.createTempFile("Tickompiler_tmp-", ".bin").apply { deleteOnExit() } }
        }
        val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

        dirs.input.forEachIndexed { index, file ->
            coroutines += GlobalScope.async {
                val compiler = Compiler(file, functions)

                try {
                    println("Compiling ${file.path}")
                    val result = compiler.compile(ByteOrder.LITTLE_ENDIAN)

                    if (result.success) {
                        val out = outputBinFiles[index].second
                        out.createNewFile()
                        val fos = FileOutputStream(out)
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
            val numSuccessful = coroutines
                    .map { it.await() }
                    .count { it }

            if (objectifying) {
                if (numSuccessful != dirs.input.size) {
                    println("""
+====================+
| COMPILATION FAILED |
+====================+
Only $numSuccessful / ${dirs.input.size} were compiled successfully. (Took ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms)
All must compile successfully to build a tkflwobj.""")
                } else {
                    val objOut = outputFile!!
                    println("""
+========================+
| COMPILATION SUCCESSFUL |
+========================+
All $numSuccessful targets were compiled successfully. (Took ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms)
Building object file ${objOut.name}...""")
                    rhmodding.tickompiler.objectify.objectify(objOut, outputBinFiles.map { it.second }, tempoFiles)
                    println("Succeeded.")
                }
            } else {
                println("""
+======================+
| COMPILATION COMPLETE |
+======================+
$numSuccessful / ${dirs.input.size} compiled successfully in ${(System.nanoTime() - nanoStart) / 1_000_000.0} ms""")
            }
        }
    }

}
