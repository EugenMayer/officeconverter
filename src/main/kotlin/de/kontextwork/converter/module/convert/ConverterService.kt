package de.kontextwork.converter.module.convert

import de.kontextwork.converter.module.convert.api.UnknownSourceFormatException
import org.apache.commons.io.FilenameUtils
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.jodconverter.core.document.DocumentFormat
import org.jodconverter.core.office.OfficeException
import org.jodconverter.core.office.OfficeManager
import org.jodconverter.local.LocalConverter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.file.Files

@Service
class ConverterService(
    private val officeManager: OfficeManager
) {
    @Throws(UnknownSourceFormatException::class, OfficeException::class)
    fun doConvert(
        targetFormat: DocumentFormat,
        inputFile: InputStream,
        inputFileName: String
    ): ByteArrayOutputStream {
        val extension = FilenameUtils.getExtension(inputFileName)
        val sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(extension)
            ?: throw UnknownSourceFormatException(
                "Cannot convert file with extension $extension since we cannot find the format in our registry"
            )

        val baseName = FilenameUtils.getBaseName(inputFileName)
        val tempWorkDir = Files.createTempDirectory("conv-").toFile()
        val tempSourceFile = tempWorkDir.resolve("$baseName.${sourceFormat.extension}")
        val tempTargetFile = tempWorkDir.resolve("$baseName.${targetFormat.extension}")

        try {
            inputFile.use { input ->
                tempSourceFile.outputStream().use { out -> input.copyTo(out) }
            }

            // Convert...
            LocalConverter.builder()
                .officeManager(officeManager)
                .build()
                .convert(tempSourceFile)
                .`as`(sourceFormat)
                .to(tempTargetFile)
                .`as`(targetFormat)
                .execute()

            return ByteArrayOutputStream().apply {
                tempTargetFile.inputStream().use { it.copyTo(this) }
            }
        } finally {
            // Clean up
            tempSourceFile.delete()
            tempTargetFile.delete()
            tempWorkDir.delete()
        }
    }

    fun isReady(): Boolean {
        if (!officeManager.isRunning) {
            return false
        }

        // Execute a dummy task
        officeManager.execute { }

        // If the above no-op task executed successfully, the application is ready
        return true
    }
}
