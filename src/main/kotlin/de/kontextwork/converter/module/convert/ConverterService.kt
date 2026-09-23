package de.kontextwork.converter.module.convert

import de.kontextwork.converter.module.convert.api.UnknownSourceFormatException
import org.apache.commons.io.FilenameUtils
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.jodconverter.core.document.DocumentFamily
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
    private companion object {
        const val FILTER_DATA = "FilterData"
        const val SINGLE_PAGE_SHEETS = "SinglePageSheets"
    }

    @Throws(UnknownSourceFormatException::class, OfficeException::class)
    @JvmOverloads
    fun doConvert(
        targetFormat: DocumentFormat,
        inputFile: InputStream,
        inputFileName: String,

        /**
         * Export every sheet onto exactly one PDF page instead of slicing it into DIN-A4 chunks.
         * `null` = decide automatically (enabled for spreadsheet -> PDF).
         */
        singlePageSheets: Boolean? = null
    ): ByteArrayOutputStream {
        val extension = FilenameUtils.getExtension(inputFileName)
        val sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(extension)
            ?: throw UnknownSourceFormatException(
                "Cannot convert file with extension $extension since we cannot find the format in our registry"
            )

        // We want to preserve the filename when creating the temporary file so macros that utilize the
        // actual filename (word/excel) are not using a random temp-filename instead. Create a unique temporary folder
        // and save the file using the original filename
        val baseName = FilenameUtils.getBaseName(inputFileName)
        val tempWorkDir = Files.createTempDirectory("conv-").toFile()
        val tempSourceFile = tempWorkDir.resolve("$baseName.${sourceFormat.extension}")
        val tempTargetFile = tempWorkDir.resolve("$baseName.${targetFormat.extension}")

        try {
            inputFile.use { input ->
                tempSourceFile.outputStream().use { out -> input.copyTo(out) }
            }

            val effectiveTargetFormat = applySinglePageSheets(
                sourceFormat = sourceFormat,
                targetFormat = targetFormat,
                requested = singlePageSheets
            )

            // Convert...
            LocalConverter.builder()
                .officeManager(officeManager)
                .build()
                .convert(tempSourceFile)
                .`as`(sourceFormat)
                .to(tempTargetFile)
                .`as`(effectiveTargetFormat)
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

    /**
     * Returns a copy of [targetFormat] with `FilterData/SinglePageSheets` added to the spreadsheet
     * store properties, or the unchanged format when the option does not apply.
     *
     * Spreadsheets usually do not fit a DIN A4 page, which makes LibreOffice split them into many
     * unreadable fragments. `SinglePageSheets` puts each sheet on a single, arbitrarily sized page
     * and thereby preserves the original layout.
     */
    private fun applySinglePageSheets(
        sourceFormat: DocumentFormat,
        targetFormat: DocumentFormat,
        requested: Boolean?
    ): DocumentFormat {
        // captures the following formats: xlsx, xltx, xls, ods, ots, fods, sxc, csv, tsv
        val isSpreadsheet = sourceFormat.inputFamily == DocumentFamily.SPREADSHEET
        val isPdfTarget = targetFormat.extension.equals("pdf", ignoreCase = true)

        val enabled = requested ?: (isSpreadsheet && isPdfTarget)
        if (!enabled || !isSpreadsheet || !isPdfTarget) {
            return targetFormat
        }

        // Keep whatever FilterData the registry already defines (currently none, but be defensive).
        val filterData = (
                targetFormat.getStoreProperties(DocumentFamily.SPREADSHEET)
                    ?.get(FILTER_DATA) as? Map<*, *>
                )
            .orEmpty().toMutableMap()

        filterData[SINGLE_PAGE_SHEETS] = true

        return DocumentFormat.builder()
            .from(targetFormat)
            .storeProperty(DocumentFamily.SPREADSHEET, FILTER_DATA, filterData)
            .build()
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
