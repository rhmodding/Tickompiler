package rhmodding.tickompiler.api

import rhmodding.tickompiler.INFO_TEXT
import java.io.PrintStream


object VersionCommand : Command("version", "v") {

    override val commandInfo: CommandInfo = CommandInfo("Displays the current version of Tickompiler.",
                                                        listOf())

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                output: PrintStream) {
        output.println(INFO_TEXT)
    }
}