package rhmodding.tickompiler

val VERSION: String = "v1.5.0"
val GITHUB: String = "https://github.com/SneakySpook/Tickompiler"
val INFO_TEXT: String = """Tickompiler: A RHM tickflow compiler/decompiler written by SneakySpook and chrislo27 in Kotlin
$VERSION
$GITHUB"""

interface TickompilerError

open class CompilerError(message: String) : RuntimeException(message), TickompilerError

open class DecompilerError(message: String) : RuntimeException(message), TickompilerError

class MissingFunctionError(message: String) : CompilerError(message)

class WrongArgumentsError(args: Long, needed: IntRange, msg: String = "") : CompilerError(
        "Wrong arg count: got $args, need $needed - $msg")
