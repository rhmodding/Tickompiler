package rhmodding.tickompiler.objectify

import com.google.gson.GsonBuilder
import rhmodding.tickompiler.Tickompiler
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

val TKFLWOBJ_PACKER_VERSION: Int = 1

fun objectify(outputFile: File, binFiles: List<File>, tempoFiles: List<File>) {
    val packerVersion = TKFLWOBJ_PACKER_VERSION
    outputFile.createNewFile()
    val zipStream = ZipOutputStream(FileOutputStream(outputFile))
    zipStream.setComment("Tickompiler tkflwobj (tickflow object) file - ${Tickompiler.VERSION} - packer version $packerVersion")
    
    val manifestObj = ManifestObj().apply {
        version = packerVersion
        bin.size = binFiles.size
        tempo.size = tempoFiles.size
    }
    zipStream.putNextEntry(ZipEntry("manifest.json"))
    zipStream.write(GsonBuilder().setPrettyPrinting().create().toJson(manifestObj).toByteArray())
    zipStream.closeEntry()
    
    binFiles.forEachIndexed { i, file -> 
        zipStream.putNextEntry(ZipEntry("bin/bin_$i.bin"))
        file.inputStream().apply {
            copyTo(zipStream)
            close()
        }
        zipStream.closeEntry()
    }

    tempoFiles.forEachIndexed { i, file ->
        zipStream.putNextEntry(ZipEntry("tempo/tempo_$i.tempo"))
        file.inputStream().apply {
            copyTo(zipStream)
            close()
        }
        zipStream.closeEntry()
    }
    
    zipStream.close()
}