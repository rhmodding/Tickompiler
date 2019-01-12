package rhmodding.tickompiler

import picocli.CommandLine
import rhmodding.tickompiler.cli.*


object Tickompiler {

    const val VERSION: String = "v1.8.0"
    const val GITHUB: String = "https://github.com/SneakySpook/Tickompiler"

    fun createAndParseCommandLine(runnable: Runnable, vararg args: String): CommandLine {
        // This is equivalent to the static method CommandLine.run(...) but with the settings desired
        return CommandLine(runnable).setToggleBooleanFlags(false).apply {
            parseWithHandlers(CommandLine.RunLast().useOut(System.out).useAnsi(CommandLine.Help.Ansi.AUTO),
                CommandLine.DefaultExceptionHandler<List<Any>>().useErr(System.err).useAnsi(CommandLine.Help.Ansi.AUTO),
                *(if (args.isEmpty()) arrayOf("--help") else args))
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        createAndParseCommandLine(TickompilerCommand(), *args)
    }
}

@CommandLine.Command(mixinStandardHelpOptions = true, versionProvider = TickompilerVersionProvider::class,
        name = "tickompiler", description = ["A RHM tickflow compiler/decompiler"],
        subcommands = [CompileCommand::class, DecompileCommand::class, PackCommand::class, ExtractCommand::class, GrabCommand::class,
            NotepadppLangCommand::class, DaemonCommand::class])
class TickompilerCommand : Runnable {
    override fun run() {
    }
}

class TickompilerVersionProvider : CommandLine.IVersionProvider {
    override fun getVersion(): Array<String> = arrayOf("Tickompiler: A RHM tickflow compiler/decompiler", Tickompiler.VERSION, Tickompiler.GITHUB, "Licensed under the MIT License")
}
