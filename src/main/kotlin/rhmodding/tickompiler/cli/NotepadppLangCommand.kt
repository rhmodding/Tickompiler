package rhmodding.tickompiler.cli

import picocli.CommandLine
import java.io.File


@CommandLine.Command(name = "notepad++", description = ["Outputs a Notepad++-suitable custom user-defined language XML file. If the output directory is not specified, it will be placed next to this executable."],
        mixinStandardHelpOptions = true)
class NotepadppLangCommand : Runnable {

    private val FILE_NAME = "tickflow.xml"

    @CommandLine.Option(names = ["-ow", "--overwrite"], description = ["Overwrite even if a file already exists."])
    var overwrite: Boolean = false

    @CommandLine.Parameters(index = "0", arity = "0..1", description = ["output file or directory"])
    var output: File = File("./")

    override fun run() {
        output.mkdirs()
        val file = if (output.isDirectory) output.resolve(FILE_NAME) else output
        if (file.exists() && !overwrite) {
            println("Cannot output ${file.name}, already exists in the target directory (${file.parentFile.absolutePath}). Please move, rename, or delete the file first.")
        } else {
            val internal = NotepadppLangCommand::class.java.getResource("/notepadplusplustickflowlang.xml")
            file.createNewFile()
            file.writeBytes(internal.readBytes())
            println("Outputted ${file.name} to ${file.parentFile.canonicalPath}\nImport it into Notepad++ via: Language > Define your language... > Import")
        }
    }
}