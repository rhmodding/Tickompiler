package rhmodding.tickompiler.api

import java.io.PrintStream
import java.util.*

object Commands {

    val commands: List<Command> =
            listOf(
                    HelpCommand,
                    CompileCommand,
                    DecompileCommand,
                    ExtractCommand,
                    PackCommand
                  )
    val commandMap: Map<String, Command> =
            commands.flatMap { cmd -> cmd.aliases.map { it to cmd } }.associate { it }

    class Flags(val flags: List<String>, val indexOfFirstArgument: Int) {
        companion object {
            fun create(args: List<String>): Flags {
                var indexOfFirstArgument: Int = -1
                val flags: List<String> = run {
                    val m = mutableListOf<String>()

                    if (args.size >= 2) {
                        for (i in (1 until args.size)) {
                            val f = args[i]
                            if (f.startsWith("-") && f.length >= 2) {
                                m += f
                            } else {
                                indexOfFirstArgument = i
                                break
                            }
                        }
                    }

                    return@run m
                }

                return Flags(flags, indexOfFirstArgument)
            }
        }
    }

    fun execute(inputArgs: List<String>, printer: PrintStream = System.out) {
        val args = if (inputArgs.isEmpty()) listOf("?") else inputArgs.toList()

        val flagsObj = Flags.create(args)
        val flags = flagsObj.flags
        val indexOfFirstArgument = flagsObj.indexOfFirstArgument

        val commandName = args.first().toLowerCase(Locale.ROOT)
        val command = commandMap[commandName] ?:
                run {
                    printer.println(
                            "Unable to find command \"$commandName\", use \"help\" for a list of commands")
                    return
                }

        command.execute(args, flagsObj, flags, indexOfFirstArgument, printer)
        // TODO daemon mode
    }

}
