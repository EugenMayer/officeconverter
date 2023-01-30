package de.kontextwork.converter.module.convert

import de.kontextwork.converter.module.convert.api.UnknownSourceFormatException
import org.apache.commons.io.FilenameUtils
import org.jodconverter.core.DocumentConverter
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.jodconverter.core.document.DocumentFormat
import org.jodconverter.core.office.OfficeException
import org.jodconverter.core.office.OfficeManager
import org.jodconverter.local.LocalConverter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
        val outputStream = ByteArrayOutputStream()
        val converter: DocumentConverter = LocalConverter.builder().officeManager(officeManager).build()

        val sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(
            FilenameUtils.getExtension(inputFileName)
        )
            ?: throw UnknownSourceFormatException(
                String.format(
                    "Cannot convert file with extension %s since we cannot find the format in our registry",
                    FilenameUtils.getExtension(inputFileName)
                )
            )

        // Convert...
        converter.convert(inputFile)
            .`as`(sourceFormat)
            .to(outputStream)
            .`as`(targetFormat)
            .execute()
        return outputStream
    }
}
