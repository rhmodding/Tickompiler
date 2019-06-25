package rhmodding.tickompiler.cli

import com.google.gson.Gson
import picocli.CommandLine
import rhmodding.tickompiler.gameputter.GamePutter
import rhmodding.tickompiler.objectify.ManifestObj
import rhmodding.tickompiler.objectify.TKFLWOBJ_PACKER_VERSION
import rhmodding.tickompiler.util.getDirectories
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.zip.ZipFile

@CommandLine.Command(name = "pack", aliases = ["p"], description = ["Pack binary, tempo, and/or tkflwobj files from a specified directory into the output file, using the specified base file.",
    "The base file can be obtained from extraction.",
    "Files must have the file extension .bin (little-endian), .tempo, or .tkflwobj",
    "The output file will be overwritten without warning.",
    "If the output file name is not specified, it will default to \"C00.bin\"."],
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

        val lookIn = dirs.map { PackTarget(it, it.name) }.toMutableList()
        val tmpFiles = mutableListOf<File>()

        val tkflwObjs: List<File> = inputFile.listFiles { _, name -> name.endsWith(".tkflwobj") }?.toList() ?: listOf()
        if (tkflwObjs.isNotEmpty()) {
            println("Detected ${tkflwObjs.size} .tkflwobj files, extracting those first...")
            tkflwObjs.forEach { f ->
                println("\tExtracting from tkflwobj ${f.name}...")
                val zipFile = ZipFile(f)
                val manifestEntry = zipFile.getEntry("manifest.json")
                val manifest = Gson().fromJson(zipFile.getInputStream(manifestEntry).let {
                    val res = it.readBytes().toString(Charsets.UTF_8)
                    it.close()
                    res
                }, ManifestObj::class.java)
                if (manifest.version <= 0) {
                    error("${f.path} - Manifest version is invalid (${manifest.version})")
                } else if (manifest.version > TKFLWOBJ_PACKER_VERSION) {
                    error("${f.path} - Manifest version is too high (${manifest.version}, max $TKFLWOBJ_PACKER_VERSION). Update Tickompiler by using the 'updates' command")
                }

                // Version 1 parsing
                if (manifest.version <= 1) {
                    for (i in 0 until manifest.bin.size) {
                        lookIn += PackTarget(File.createTempFile("Tickompiler_tmp-", ".bin").apply {
                            zipFile.getInputStream(zipFile.getEntry("bin/bin_$i.bin")).also { stream ->
                                val fos = FileOutputStream(this)
                                stream.copyTo(fos)
                                fos.close()
                                stream.close()
                            }
                            deleteOnExit()
                            tmpFiles += this
                        }, "${f.name}[bin_$i.bin]")
                    }
                    for (i in 0 until manifest.tempo.size) {
                        lookIn += PackTarget(File.createTempFile("Tickompiler_tmp-", ".tempo").apply {
                            zipFile.getInputStream(zipFile.getEntry("tempo/tempo_$i.tempo")).also { stream ->
                                val fos = FileOutputStream(this)
                                stream.copyTo(fos)
                                fos.close()
                                stream.close()
                            }
                            deleteOnExit()
                            tmpFiles += this
                        }, "${f.name}[tempo_$i.tempo]")
                    }
                }

                zipFile.close()
            }
        }

        for ((file, descriptor) in lookIn) {
            println("Packing $descriptor...")
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

        tmpFiles.forEach { it.delete() }

        println("Done.")
    }

}

data class PackTarget(val file: File, val descriptor: String)
