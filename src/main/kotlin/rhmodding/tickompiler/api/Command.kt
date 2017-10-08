package rhmodding.tickompiler.api

import java.io.PrintStream


abstract class Command(val aliases: List<String>) {

    constructor(name: String) : this(listOf(name))
    constructor(vararg names: String) : this(listOf(*names))

    init {
        if (aliases.isEmpty())
            error("Cannot have no aliases")
    }

    abstract val commandInfo: CommandInfo

    abstract fun execute(args: List<String>, flagsObj: Commands.Flags, flags: List<String>, indexOfFirstArgument: Int, output: PrintStream)

    open fun onThrowable(throwable: Throwable, output: PrintStream) {
        throwable.printStackTrace(output)
    }

}

class CommandInfo(val usage: String, val description: List<String>,
                  val flags: List<FlagInfo> = listOf())

class FlagInfo(val flags: List<String>, val description: List<String>)
