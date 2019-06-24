package rhmodding.tickompiler.util

import java.io.File


class Directories(val input: List<File>, val output: List<File>)

fun getDirectories(inputFile: File?, outputFile: File?, firstFilter: (String) -> Boolean, outputExtension: String, ignoreDir: Boolean = false): Directories {
    val input: MutableList<File> = mutableListOf()
    val output: MutableList<File> = mutableListOf()

    if (inputFile != null) {
        val first: File = inputFile
        if (first.isFile) {
            input += first
        } else if (first.isDirectory) {
            input += first.listFiles { _, name -> firstFilter(name) }.filter { it.isFile }
        }

        val second: File? = outputFile
        if (second?.isFile == true) {
            if (input.size > 1 && !ignoreDir)
                throw IllegalArgumentException("Output option cannot be a file when the input is a directory!")

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
