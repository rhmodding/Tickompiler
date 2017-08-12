package rhmodding.tickompiler

import rhmodding.tickompiler.api.Commands


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Commands.execute(args.toList())
    }

}
