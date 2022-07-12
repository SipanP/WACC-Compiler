package extension

import getEachFile
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class OptimisationTest {
    private val map = getAssemblyMap()

    /**
     * Tests the assembly code produced by compiling some additional WACC example files,
     * comparing them to an optimised version of the assembly originally produced
     * (Testing assembly has been optimised)
     */
    @ParameterizedTest
    @MethodSource("testFiles")
    fun assemblyIsOptimised(file: File) {
        val name = file.nameWithoutExtension
        val optimisedCode = map[name]

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

        val assemblyCode = File("$name.s").inputStream().bufferedReader().use { it.readText() }

        val success = (assemblyCode == optimisedCode)

        if (success) {
            println("- PASS: Assembly Optimised for $name")
        } else {
            println("- FAIL: Assembly NOT Optimised for $name")
            println("------REFERENCE Assembly------")
            println(optimisedCode)
            println("------OUR OUTPUT------")
            println(assemblyCode)
        }
        ProcessBuilder("rm", "$name.s").start().waitFor(20, TimeUnit.SECONDS)
        assertTrue(success)
    }


    companion object {
        @JvmStatic
        fun testFiles(): List<File> {
            return getEachFile(File("examples/reference_files/valid/optimisations"))
        }
    }

    /**
     * Returns a mapping of new WACC example files to their optimised assembly
     * code
     */
    private fun getAssemblyMap(): HashMap<String, String> {
        val map = HashMap<String, String>()
        val root = "examples/reference_files/valid/optimisations"
        for (file in getEachFile(File(root))) {
            val assemblyFile =
                "examples/reference_output/" +
                        file.invariantSeparatorsPath.split(".wacc").first()
                            .split("examples/reference_files/valid/")
                            .last() + "/assembly.txt"
            val assemblyText =
                File(assemblyFile).inputStream().bufferedReader().use { it.readText() }
            map[file.nameWithoutExtension] = assemblyText
        }
        return map
    }
}