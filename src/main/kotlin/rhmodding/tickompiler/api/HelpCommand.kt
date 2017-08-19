package rhmodding.tickompiler.api

import rhmodding.tickompiler.INFO_TEXT
import java.io.PrintStream
import java.util.*


object HelpCommand : Command("help", "?") {

    override val commandInfo: CommandInfo = CommandInfo("[command name]", listOf("Displays this help message"))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                         output: PrintStream) {
        val builder = StringBuilder()

        fun spitOutInfo(cmd: Command) {
            val info = cmd.commandInfo
            builder.append("${cmd.aliases.joinToString(", ")} ${info.usage}\n")

            info.description.forEach {
                builder.append("  - $it\n")
            }

            if (info.flags.isNotEmpty()) {
                builder.append("  - Flags:\n")
                info.flags.forEach {
                    builder.append("    - ${it.flags.joinToString(", ")}\n")
                    it.description.forEach { desc ->
                        builder.append("      - $desc\n")
                    }
                }
            }

            builder.append("\n")
        }

        if (indexOfFirstArgument != -1) {
            val cmd = Commands.commandMap[args[indexOfFirstArgument].toLowerCase(Locale.ROOT)]
            if (cmd == null) {
                output.println("Could not display help: command ${args[1]} not found")
                return
            }

            spitOutInfo(cmd)
        } else {
            builder.append("""
$INFO_TEXT

Commands:
""")
            Commands.commands.forEach(::spitOutInfo)
        }

        output.println(builder.toString())
    }

}