package rhmodding.tickompiler.cli

import picocli.CommandLine
import rhmodding.tickompiler.Tickompiler
import rhmodding.tickompiler.TickompilerCommand
import java.util.regex.Pattern

@CommandLine.Command(name = "daemon", description = ["Runs Tickompiler in daemon mode.",
    "This will provide a continuously running program which makes use of JIT (just-in-time compilation) to speed up future compilations/decompilations.",
    "You can optionally provide the first command you want to run as arguments to this command.",
    "CTRL+C will kill the program. Typing 'stop' or 'exit' will work too."],
        mixinStandardHelpOptions = true)
class DaemonCommand : Runnable {

    @CommandLine.Parameters(index = "0", arity = "0..*", description = ["First command to execute along with its arguments, if any."])
    var firstCommand: List<String> = listOf()

    override fun run() {
        println("Running in daemon mode: press CTRL+C or type 'stop' or 'exit' to terminate\nType '-h' for help\n${Tickompiler.VERSION}\n${Tickompiler.GITHUB}\n")

        var input: String = (if (firstCommand.isNotEmpty()) {
            // we have a first command to immediately execute
            firstCommand.joinToString(" ")
        } else readLine())?.trim() ?: return

        while (!input.equals("stop", true) && !input.equals("exit", true)) {
            if (input.isEmpty())
                continue

            val list = mutableListOf<String>()
            val m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input)
            while (m.find())
                list.add(m.group(1).replace("\"", ""))

            Tickompiler.createAndParseCommandLine(TickompilerCommand(), *list.toTypedArray())

            println("--------------------------------")

            input = readLine()?.trim() ?: return
        }
    }

}