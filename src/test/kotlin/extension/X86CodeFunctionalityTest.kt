package extension

import backend.MapOfFilesToOutput
import getEachFile
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class X86CodeFunctionalityTest {
    private val map = MapOfFilesToOutput.getMap()

    /**
     * Tests the output of running all valid WACC example files, after compiling to
     * x86 assembly code, comparing them to the output of the reference compiler
     * (Testing functional correctness)
     */
    @ParameterizedTest
    @MethodSource("testFiles")
    fun assemblyIsFunctionallyCorrect(file: File) {
        val name = file.nameWithoutExtension
        val refOutput = map[name]!!.first
        val refExit = map[name]!!.second

        ProcessBuilder("./compile", file.invariantSeparatorsPath, "-x86").start()
            .waitFor(20, TimeUnit.SECONDS)
        ProcessBuilder(
            "gcc", "-g", "-o", name, "$name.s", "-no-pie"
        ).start().waitFor(20, TimeUnit.SECONDS)

        var output: String
        val inputFile = File("examples/reference_output/inputFiles/$name/input.txt")
        val process = if (inputFile.exists()) {
            ProcessBuilder("./$name").redirectInput(
                inputFile
            ).start()
        } else {
            ProcessBuilder("./$name").start()
        }

        process.waitFor(20, TimeUnit.SECONDS)

        process.inputStream.reader(Charsets.UTF_8).use {
            output = it.readText()
        }

        val success = ((refOutput == output) && (refExit == process.exitValue()))

        if (success) {
            println("- PASSED $name")
        } else {
            println("- FAILING $name -")
            println("------REFERENCE OUTPUT------")
            println(refOutput)
            println("------OUR OUTPUT------")
            println(output)
            println("----------------------")
            println("REFERENCE EXIT CODE: $refExit")
            println("OUR EXIT CODE: ${process.exitValue()}")
            println("----------------------")
        }
        ProcessBuilder("rm", "$name.s", name).start().waitFor(20, TimeUnit.SECONDS)
        assertTrue(success)
    }


    companion object {
        @JvmStatic
        fun testFiles(): List<File> {
            val root = "examples/reference_files/valid"
            /**
             * We have excluded some tests for arrays, as a different address has been used
             * for x86 compared to the ARM architecture- they are still functionally correct.
             * We have also excluded the pair and pointer test files
             */
            return getEachFile(
                File(root),
                listOf(
                    File("$root/array/array.wacc"),
                    File("$root/array/arrayNested.wacc"),
                    File("$root/array/arrayPrint.wacc"),
                    File("$root/array/printRef.wacc"),
                    File("$root/function/simple_functions/functionManyArguments.wacc"),
                    File("$root/function/simple_functions/functionReturnPair.wacc"),
                    File("$root/runtimeErr/arrayOutOfBounds/arrayOutOfBounds.wacc"),
                    File("$root/runtimeErr/arrayOutOfBounds/arrayOutOfBoundsWrite.wacc"),
                    File("$root/scope/printAllTypes.wacc"),
                    File("$root/inputFiles/readPair.wacc")
                ) + getEachFile(File("$root/pairs/")) + getEachFile(File("$root/pointers/"))
            )
        }
    }
}