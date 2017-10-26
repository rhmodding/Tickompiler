package rhmodding.tickompiler.api

import java.io.File
import java.io.PrintStream


object NotepadppLangCommand : Command("notepad++") {

    private const val FILE_NAME = "tickflow.xml"

    override val commandInfo = CommandInfo("[output directory]",
                                           listOf("Outputs a Notepad++-suitable custom user-defined language XML file. The file name will be $FILE_NAME. If the output directory is not specified, it will be placed next to this executable."),
                                           listOf(
                                                   FlagInfo(listOf("-ow"), listOf("Overwrite even if a file already exists."))
                                                 ))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                output: PrintStream) {
        val folder = File(if (indexOfFirstArgument >= 0) args[indexOfFirstArgument] else "")
        folder.mkdirs()
        val file = folder.resolve(FILE_NAME)
        if (file.exists() && "-ow" !in flags) {
            output.println("Cannot output $FILE_NAME, already exists in the target directory (${folder.absolutePath}). Please move, rename, or delete the file first.")
        } else {
            val internal = NotepadppLangCommand::class.java.getResource("/notepadplusplustickflowlang.xml")
            file.writeBytes(internal.readBytes())
            output.println("Outputted $FILE_NAME to ${folder.absolutePath}. Import it into Notepad++ via: Language > Define your language... > Import")
        }
    }
}