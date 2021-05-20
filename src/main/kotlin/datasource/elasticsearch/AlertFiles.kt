package datasource.elasticsearch

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

object AlertFiles {
    fun collect(rootPath: String): List<File> {
        return Files.walk(Paths.get(rootPath))
            .filter(Files::isRegularFile)
            .filter {
                it.toFile().name == "alerts.json"
            }.map {
                it.toFile()
            }.toList()
            .sortedBy { it.absolutePath }
    }
}