package rhmodding.tickompiler.test

import rhmodding.tickompiler.compiler.TickflowParser
import org.parboiled.Parboiled
import org.parboiled.parserunners.TracingParseRunner
import org.parboiled.support.ParseTreeUtils
import org.parboiled.support.ParsingResult

fun main(args: Array<String>) {

    val tickflow: String = """
potato = 0x9
macro<0b11> [potato]
"""

    println("TEST PARSING OF:\n$tickflow\n")

    val parser = Parboiled.createParser(TickflowParser::class.java)
    val result: ParsingResult<Any> = TracingParseRunner<Any>(parser.TickflowCode()).run(tickflow)

    println(result.matched)
    println(ParseTreeUtils.printNodeTree(result))
    result.valueStack.forEach(::println)

}
