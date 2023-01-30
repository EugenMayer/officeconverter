package de.kontextwork.converter.module.convert.controller

import de.kontextwork.converter.base.FileNameUtils
import de.kontextwork.converter.module.convert.ConverterService
import de.kontextwork.converter.module.convert.api.UnknownSourceFormatException
import org.apache.commons.io.FilenameUtils
import org.apache.logging.log4j.kotlin.Logging
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.jodconverter.core.office.OfficeException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.IOException

@RestController
@RequestMapping("/conversion")
class ConversionController(
    private val converterService: ConverterService,
    private val fileNameUtils: FileNameUtils
) : Logging {
    @PostMapping(path = [""])
    @Throws(IOException::class)
    fun convert(
        @RequestParam(name = "format", defaultValue = "pdf") targetFormatExt: String,
        @RequestParam("file") inputMultipartFile: MultipartFile
    ): ResponseEntity<*> {
        val targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(targetFormatExt)
        if (targetFormat == null) {
            logger.error(String.format("Unknown conversion target %s", targetFormatExt))
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build<Any>()
        }

        // we preserve the extension only. Anything else could be dangerous
        val inputFilename = fileNameUtils.extractFilenameOnly(inputMultipartFile.originalFilename!!)

        val convertedFile: ByteArrayOutputStream = try {
            converterService.doConvert(targetFormat, inputMultipartFile.inputStream, inputFilename)
        } catch (unknownSourceFormatException: UnknownSourceFormatException) {
            logger.error(unknownSourceFormatException.message!!)
            return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(unknownSourceFormatException.message)
        } catch (officeException: OfficeException) {
            logger.error(officeException.message!!)
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build<Any>()
        }
        // else

        val headers = HttpHeaders()
        val targetFilename = "${FilenameUtils.getBaseName(inputFilename)}.${targetFormat.extension}"
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$targetFilename")
        headers.contentType = MediaType.parseMediaType(targetFormat.mediaType)
        return ResponseEntity.ok().headers(headers).body(convertedFile.toByteArray())
    }


}
