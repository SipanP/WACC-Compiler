package backend

import getEachFile
import java.io.File

/**
 * Singleton class storing a mapping of valid WACC example files to their corresponding
 * outputs and exit codes (if any) when passed through the reference compiler, cached in the
 * reference_output directory
 */
object MapOfFilesToOutput {
    private val map = HashMap<String, Pair<String, Int>>()

    init {
        val root = "examples/reference_files/valid/"
        for (file in getEachFile(File(root))) {
            val newFileRoot = "examples/reference_output/" +
                    file.invariantSeparatorsPath.split(".wacc").first()
                        .split(root)
                        .last()
            val outputFile = File("$newFileRoot/output.txt")
            val errorFile = File("$newFileRoot/error.txt")

            val output =
                if (outputFile.exists()) {
                    outputFile.inputStream().bufferedReader().use { it.readText() }
                } else {
                    ""
                }
            val exitCode =
                if (errorFile.exists()) {
                    errorFile.inputStream().bufferedReader().use { it.readText() }.toInt()
                } else {
                    0
                }

            map[file.nameWithoutExtension] = Pair(output, exitCode)
        }
    }

    fun getMap(): HashMap<String, Pair<String, Int>> {
        return map
    }
}



