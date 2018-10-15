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

@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "unused"})
@RestController
@RequestMapping("/conversion")
public class ConversionRestController {
    @Autowired
    private ConverterService converterService;

    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<?> convert(@RequestParam(name="format", defaultValue = "pdf") final String targetFormatExt, @RequestParam("file") final MultipartFile inputMultipartFile) throws IOException, OfficeException {
        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(targetFormatExt);

        if (targetFormat == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        ByteArrayOutputStream convertedFile = converterService.doConvert(targetFormat, inputMultipartFile.getInputStream(), inputMultipartFile.getOriginalFilename());

        final HttpHeaders headers = new HttpHeaders();
        String targetFilename = String.format("%s.%s",FilenameUtils.getBaseName(inputMultipartFile.getOriginalFilename()),targetFormat.getExtension());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + targetFilename );
        headers.setContentType(MediaType.parseMediaType(targetFormat.getMediaType()));
        return ResponseEntity.ok().headers(headers).body(convertedFile.toByteArray());
    }
}

