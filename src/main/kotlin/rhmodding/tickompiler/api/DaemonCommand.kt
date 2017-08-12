package rhmodding.tickompiler.api

import rhmodding.tickompiler.GITHUB
import rhmodding.tickompiler.VERSION
import java.io.PrintStream
import java.util.*
import java.util.regex.Pattern


object DaemonCommand : Command("daemon") {
    override val commandInfo: CommandInfo =
            CommandInfo("[first command]",
                        listOf("Runs Tickompiler in daemon mode.",
                               "This will provide a continuously running program which makes use of JIT (just-in-time compilation) to speed up future compilations/decompilations.",
                               "You can optionally provide the first command you want to run as arguments to this command.",
                               "CTRL+C will kill the program. Typing stop or exit will work too."))

    override fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int,
                output: PrintStream) {

        output.println("Running in daemon mode: CTRL+C or stop or exit to finish\n$VERSION\n$GITHUB\n")

        var input: String = (if (indexOfFirstArgument != -1) {
            // we have a first command to immediately execute
            args.drop(1).joinToString(" ")
        } else readLine())?.trim() ?: return

        while (!input.equals("stop", true) && !input.equals("exit", true)) {
            if (input.isEmpty())
                continue

            val list = mutableListOf<String>()
            val m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input)
            while (m.find())
                list.add(m.group(1).replace("\"", ""))

            val command = Commands.commandMap[list.first().toLowerCase(Locale.ROOT)]
            if (command == null) {
                output.println("Command not found: ${list.first()}")
            } else {
                Commands.execute(list)
            }

            output.println("--------------------------------")

            input = readLine()?.trim() ?: return
        }

    }
}