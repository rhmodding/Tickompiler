package rhmodding.tickompiler.util

import rhmodding.tickompiler.api.Commands
import java.io.File


class Directories(val input: List<File>, val output: List<File>)

fun getDirectories(flags: Commands.Flags, args: List<String>, firstFilter: (String) -> Boolean, outputExtension: String, ignoreDir: Boolean = false): Directories {
    val input: MutableList<File> = mutableListOf()
    val output: MutableList<File> = mutableListOf()

    val indexOfFirstArgument = flags.indexOfFirstArgument

    if (indexOfFirstArgument != -1) {
        val first: File = File(args[indexOfFirstArgument])
        if (first.isFile) {
            input += first
        } else if (first.isDirectory) {
            input += first.listFiles { _, name -> firstFilter(name) }.filter { it.isFile }
        }

        val second: File? = if (indexOfFirstArgument + 1 < args.size) File(
                args[indexOfFirstArgument + 1]) else null
        if (second?.isFile == true) {
            if (input.size > 1 && !ignoreDir)
                throw IllegalArgumentException(
                        "Output option cannot be a file when the input is a directory!")

            output += second

        } else {
            second?.mkdirs()
            input.mapTo(output) { file ->
                File(second, file.nameWithoutExtension + "" + outputExtension)
            }
        }

    }

    return Directories(input, output)
}
