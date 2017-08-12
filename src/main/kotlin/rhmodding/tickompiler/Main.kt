package rhmodding.tickompiler

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import rhmodding.tickompiler.compiler.Compiler
import rhmodding.tickompiler.decompiler.CommentType
import rhmodding.tickompiler.decompiler.Decompiler
import rhmodding.tickompiler.gameextractor.GameExtractor
import rhmodding.tickompiler.gameextractor.TABLE_OFFSET
import rhmodding.tickompiler.gameextractor.TEMPO_TABLE
import rhmodding.tickompiler.gameextractor.getName
import rhmodding.tickompiler.gameputter.GamePutter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.run


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        var args = args
        if (args.isEmpty()) {
            args = arrayOf("?")
        }

        var indexOfFirstNotFlag: Int = -1
        val flags: List<String> = run {
            val m = mutableListOf<String>()

            if (args.size >= 2) {
                for (i in (1 until args.size)) {
                    val f = args[i]
                    if (f.startsWith("-") && f.length >= 2) {
                        m += f
                    } else {
                        indexOfFirstNotFlag = i
                        break
                    }
                }
            }

            return@run m
        }

        fun getDirectories(firstFilter: (String) -> Boolean, outputExtension: String, ignoreDir: Boolean = false): Directories {
            val input: MutableList<File> = mutableListOf()
            val output: MutableList<File> = mutableListOf()

            if (indexOfFirstNotFlag != -1) {
                val first: File = File(args[indexOfFirstNotFlag])
                if (first.isFile) {
                    input += first
                } else if (first.isDirectory) {
                    input += first.listFiles { _, name -> firstFilter(name) }.filter { it.isFile }
                }

                val second: File? = if (indexOfFirstNotFlag + 1 < args.size) File(
                        args[indexOfFirstNotFlag + 1]) else null
                if (second?.isFile == true) {
                    if (input.size > 1 && !ignoreDir)
                        throw IllegalArgumentException(
                                "Output option cannot be a file when the input is a directory!")

                    output += second

                } else {
                    second?.mkdirs()
                    input.mapTo(output) { file ->
                        File(second, file.nameWithoutExtension + "." + outputExtension)
                    }
                }

            }

            return Directories(input, output)
        }

        when (args.first()) {
            "help", "?" -> {
                println("""
Tickompiler: A RHM tickflow compiler/decompiler written by SneakySpook and chrislo27 in Kotlin
$VERSION
$GITHUB

Commands:
help, ?
  - Show this help message

compile, c [flags] <input file or dir> [output file or dir]
  - Compile file(s) and output them to the file/directory specified.
  - Files must be with the file extension .tickflow
  - Files will be overwritten without warning.
  - If the output is not specified, the file will be a (little-endian) .bin file with the same name.
  - Flags:
	- -c
	  - Continue even with errors
    - -m
      - Compile with Megamix function set (default)
    - -ds
      - Compile with RHDS function set

decompile, d [flags] <input file or dir> [output file or dir]
  - Compile file(s) and output them to the file/directory specified.
  - Files must be with the file extension .bin (little-endian)
  - Files will be overwritten without warning.
  - If the output is not specified, the file will be a .tickflow file with the same name.
  - Flags:
	- -c
      - Continue even with errors
	- -nc
 	  - No comments
    - -bytecode
      - Have a comment with the bytecode - no comments overrides this
	- -nm
	  - No metadata (use when decompiling snippets instead of full files)
    - -m
      - Compile with Megamix function set (default)
    - -ds
      - Compile with RHDS function set. Also disables Megamix-specific metadata.

extract, e [flags] <code file> [output dir]
  - Extract binary files from a decrypted code.bin file and output them to the directory specified.
  - File must be with the files extension .bin (little-endian)
  - Files will be overwritten without warning.
  - If the output is not specified, the directory will have the same name as the file.
  - A base.bin file will also be created in the output directory. This is a base C00.bin file.
  - Flags:
    - -a
      - Extract all subroutines of game engines, as opposed to only ones used by the games.
      - Note that this potentially includes sequels and prequels to the game.
    - -d
      - Immediately decompile all extracted games, with enhanced features such as meaningful marker names.
      - Will be extracted into a "decompiled" directory in the output directory.
    - -t
      - Extract tempo files. These will be written as .tempo files in a "tempo" folder in the output directory.

pack, p <input dir> <base file> [output file]
  - Pack binary and tempo files from a specified directory into the output file, using the specified base file.
  - Base file can be obtained from extraction
  - Files must be with the file extension .bin (little-endian) or .tempo
  - Output file will be overwritten without warning.
  - If the output is not specified, it will default to "C00.bin".
""".replace("\t", "    "))
            }
            "c", "compile" -> {
                val nanoStart: Long = System.nanoTime()
                val dirs = getDirectories({ s -> s.endsWith(".tickflow") }, "bin")
                val functions = when {
                    flags.contains("-m") -> MegamixFunctions
                    flags.contains("-ds") -> DSFunctions
                    else -> MegamixFunctions
                }

                println("Compiling ${dirs.input.size} file(s)")

                val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

                dirs.input.forEachIndexed { index, file ->
                    coroutines += async(CommonPool) {
                        val compiler = Compiler(file, functions)

                        try {
                            println("Compiling ${file.path}")
                            val result = compiler.compile(ByteOrder.BIG_ENDIAN)

                            if (result.success) {
                                dirs.output[index].createNewFile()
                                val fos = FileOutputStream(dirs.output[index])
                                fos.write(result.data.array())
                                fos.close()

                                println("Compiled ${file.path} -> ${result.timeMs} ms")
                                return@async true
                            }
                        } catch (e: Exception) {
                            if (flags.contains("-c")) {
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
            "d", "decompile" -> {
                val nanoStart = System.nanoTime()
                val dirs = getDirectories({ s -> s.endsWith(".bin") }, "tickflow")
                val functions = when {
                    flags.contains("-m") -> MegamixFunctions
                    flags.contains("-ds") -> DSFunctions
                    else -> MegamixFunctions
                }

                val coroutines: MutableList<Deferred<Boolean>> = mutableListOf()

                println("Decompiling ${dirs.input.size} file(s)")
                dirs.input.forEachIndexed { index, file ->
                    coroutines += async(CommonPool) {
                        val decompiler = Decompiler(Files.readAllBytes(file.toPath()),
                                                    ByteOrder.BIG_ENDIAN, functions)

                        try {
                            println("Decompiling ${file.path}")
                            val result = decompiler.decompile(if (flags.contains(
                                    "-nc")) CommentType.NONE else if ("-bytecode" in flags) CommentType.BYTECODE else CommentType.NORMAL,
                                                              !flags.contains("-nm") && functions == MegamixFunctions)

                            dirs.output[index].createNewFile()
                            val fos = FileOutputStream(dirs.output[index])
                            fos.write(result.second.toByteArray(Charset.forName("UTF-8")))
                            fos.close()

                            println("Decompiled ${file.path} -> ${result.first} ms")
                            return@async true
                        } catch (e: RuntimeException) {
                            if (flags.contains("-c")) {
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
            "e", "extract" -> {
                if (indexOfFirstNotFlag == -1) {
                    throw IllegalArgumentException("A code file needs to be specified!")
                }
                val codebin = File(args[indexOfFirstNotFlag])
                val codeBuffer = ByteBuffer.wrap(Files.readAllBytes(codebin.toPath())).order(ByteOrder.LITTLE_ENDIAN)
                val folder = File(when {
                    indexOfFirstNotFlag + 1 < args.size -> args[indexOfFirstNotFlag+1]
                    else -> codebin.nameWithoutExtension
                })
                folder.mkdirs()
                val decompiledFolder = File(folder, "decompiled")
                if (flags.contains("-d")) {
                    decompiledFolder.mkdirs()
                }
                for (i in 0 until 104) {
                    println("Extracting ${codeBuffer.getName(i)}")
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
                        println("Decompiling ${codeBuffer.getName(i)}")
                        val r = decompiler.decompile(CommentType.NORMAL, true, "    ", result.first)
                        val f = FileOutputStream(File(decompiledFolder, codeBuffer.getName(i) + ".tickflow"))
                        f.write(r.second.toByteArray(Charset.forName("UTF-8")))
                        f.close()
                        println("Decompiled ${codeBuffer.getName(i)} -> ${r.first} ms")
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
                        println("Extracted tempo file ${tempoPair.first}")
                    }
                }
                val tableList = ByteArray(104*53)
                codeBuffer.position(TABLE_OFFSET -0x100000)
                codeBuffer.get(tableList, 0, 104*53)
                val tempoList = ByteArray(16*0x1DD)
                codeBuffer.position(TEMPO_TABLE -0x100000)
                codeBuffer.get(tempoList, 0, 16*0x1DD)
                val fos = FileOutputStream(File(folder, "base.bin"))
                fos.write(tableList)
                fos.write(tempoList)
                fos.close()
            }
            "p", "pack" -> {
                val dirs = getDirectories({ it.endsWith(".bin") || it.endsWith(".tempo") }, "", true).input
                val base = Files.readAllBytes(Paths.get(args[indexOfFirstNotFlag + 1]))
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
                val file = File(when {
                    indexOfFirstNotFlag + 2 < args.size -> args[indexOfFirstNotFlag + 2]
                    else -> "C00.bin"
                })
                val fos = FileOutputStream(file)
                fos.write(baseBuffer.array())
                fos.write(out.toByteArray())
                fos.close()
            }
        }
    }

}

private data class Directories(val input: List<File>, val output: List<File>)
