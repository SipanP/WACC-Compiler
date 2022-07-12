package backend

import getEachFile
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class ARMCodeFunctionalityTest {
    private val map = MapOfFilesToOutput.getMap()

    /**
     * Tests the output of running all valid WACC example files, after compiling to
     * ARM assembly code, comparing them to the output of the reference compiler
     * (Testing functional correctness)
     */
    @ParameterizedTest
    @MethodSource("testFiles")
    fun assemblyIsFunctionallyCorrect(file: File) {
        val name = file.nameWithoutExtension
        val refOutput = map[name]!!.first
        val refExit = map[name]!!.second

        ProcessBuilder("./compile", file.invariantSeparatorsPath, "-o").start()
            .waitFor(20, TimeUnit.SECONDS)
        ProcessBuilder(
            "arm-linux-gnueabi-gcc",
            "-o",
            name,
            "-mcpu=arm1176jzf-s",
            "-mtune=arm1176jzf-s",
            "$name.s"
        ).start().waitFor(20, TimeUnit.SECONDS)

        var output: String
        val inputFile = File("examples/reference_output/inputFiles/$name/input.txt")
        val process = if (inputFile.exists()) {
            ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi", name).redirectInput(
                inputFile
            ).start()
        } else {
            ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi", name).start()
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
            return getEachFile(File("examples/reference_files/valid/"))
        }
    }
}
