package de.kontextwork.converter.controller;

import de.kontextwork.converter.service.ConverterService;
import org.apache.commons.io.FilenameUtils;

import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/conversion")
public class ConversionRestController {

    @Autowired
    private ConverterService converterService;

    @RequestMapping(path = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> convert(@RequestParam(name="format", defaultValue = "pdf") final String targetFormatExt, @RequestParam("data") final MultipartFile inputMultipartFile) throws IOException, OfficeException {
        if (!converterService.validateFormat(targetFormatExt)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(targetFormatExt);
        ByteArrayOutputStream convertedFile = converterService.doConvert(targetFormat, inputMultipartFile.getInputStream(), inputMultipartFile.getName());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(targetFormat.getMediaType()));
        headers.add(
                "Content-Disposition",
                "attachment; filename="
                        + FilenameUtils.getBaseName(inputMultipartFile.getOriginalFilename())
                        + "."
                        + targetFormat.getExtension());
        return ResponseEntity.ok().headers(headers).body(convertedFile.toByteArray());
    }
}
