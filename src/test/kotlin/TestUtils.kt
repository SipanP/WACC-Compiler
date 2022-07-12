import java.io.File

/**
 * Recursively finds each file in a given directory
 */
fun getEachFile(file: File): List<File> {
    val listOfFiles = emptyList<File>().toMutableList()
    if (file.isDirectory) {
        for (subFile in file.listFiles()!!) {
            listOfFiles += getEachFile(subFile)
        }
    } else {
        listOfFiles += file
    }
    return listOfFiles
}

/**
 * Recursively finds each file in a given directory, excluding
 * specified files
 */
fun getEachFile(file: File, exclusions: List<File>): List<File> {
    val listOfFiles = emptyList<File>().toMutableList()
    if (file.isDirectory) {
        for (subFile in file.listFiles()!!) {
            listOfFiles += getEachFile(subFile, exclusions)
        }
    } else {
        if (!exclusions.contains(file)) {
            listOfFiles += file
        }
    }
    return listOfFiles
}