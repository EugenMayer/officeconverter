package de.kontextwork.converter.service;

import org.apache.commons.io.FilenameUtils;
import org.jodconverter.DocumentConverter;
import org.jodconverter.LocalConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class ConverterService {
    private OfficeManager officeManager;

    @Autowired
    public ConverterService(final OfficeManager officeManager) {
        this.officeManager = officeManager;
    }

    public ByteArrayOutputStream doConvert(final DocumentFormat targetFormat, final InputStream inputFile, String inputFileName) throws OfficeException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final DocumentConverter converter =
                LocalConverter.builder()
                        .officeManager(officeManager)
                        .build();

        final DocumentFormat sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(
                FilenameUtils.getExtension(inputFileName));

        // Convert...
        converter.convert(inputFile)
                 .as(sourceFormat)
                 .to(outputStream)
                 .as(targetFormat)
                 .execute();

        return outputStream;
    }
}
