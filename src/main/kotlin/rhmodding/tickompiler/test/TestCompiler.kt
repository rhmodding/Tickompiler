package rhmodding.tickompiler.test

import rhmodding.tickompiler.MegamixFunctions
import rhmodding.tickompiler.compiler.Compiler
import rhmodding.tickompiler.output.Outputter
import java.nio.ByteOrder

fun main(args: Array<String>) {

    val tickflow: String = """
#index 0
#start 0
#assets 0x1C0
async_call async0, 0
async_call async1, 0
stop
async0:
macro 0x56
rest 48
stop
async1:
macro "0x57\\\""
rest 48
stop
"""

//	tickflow = """
//rest 96
//rest 148
//"""

    println("TEST COMPILE OF:")
    println(tickflow + "\n")

    val compiler = Compiler(tickflow, MegamixFunctions)

    val bo = ByteOrder.LITTLE_ENDIAN
    val out = compiler.compile(bo)
    println(out)

    println()
    val buffer = out.data.array()
    println(if (out.success) "===============\nCOMPILE SUCCESS\n===============\n"
            else "===============\nCOMPILE FAILURE\n===============")
    println(bo.toString())
    println(Outputter.toHexBytes(buffer, !true))

    Thread.sleep(250L)
}
