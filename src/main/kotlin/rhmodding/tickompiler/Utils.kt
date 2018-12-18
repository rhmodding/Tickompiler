package rhmodding.tickompiler

interface TickompilerError

open class CompilerError(message: String) : RuntimeException(message), TickompilerError

open class DecompilerError(message: String) : RuntimeException(message), TickompilerError

class MissingFunctionError(message: String) : CompilerError(message)

class WrongArgumentsError(args: Long, needed: IntRange, msg: String = "") : CompilerError(
        "Wrong arg count: got $args, need $needed - $msg")
