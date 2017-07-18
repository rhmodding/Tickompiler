package chrislo27.tickompiler

val VERSION: String = "v1.3.0"

val GITHUB: String = "https://github.com/SneakySpook/Tickompiler"

interface TickompilerError

open class CompilerError(message: String) : RuntimeException(message), TickompilerError

open class DecompilerError(message: String) : RuntimeException(message), TickompilerError

class MissingFunctionError(message: String) : CompilerError(message)

class WrongArgumentsError(args: Long, needed: IntRange, msg: String = "") : CompilerError(
        "Wrong arg count: got $args, need $needed - $msg")
