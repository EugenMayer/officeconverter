package de.kontextwork.converter.base

import org.springframework.stereotype.Component
import java.nio.file.Paths
import kotlin.io.path.name

@Component
class FileNameUtils {
    fun extractFilenameOnly(originalFileName: String): String {
        return Paths.get(originalFileName).name
    }
}
