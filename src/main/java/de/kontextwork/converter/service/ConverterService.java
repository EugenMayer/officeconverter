package de.kontextwork.converter.service;

import de.kontextwork.converter.service.api.UnknownSourceFormat;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConverterService
{
  private final OfficeManager officeManager;

  public ByteArrayOutputStream doConvert(
    final DocumentFormat targetFormat,
    final InputStream inputFile,
    String inputFileName
  ) throws UnknownSourceFormat, OfficeException
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    final DocumentConverter converter = LocalConverter.builder()
      .officeManager(officeManager)
      .build();

    final DocumentFormat sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(
      FilenameUtils.getExtension(inputFileName)
    );

    if (sourceFormat == null) {
      throw new UnknownSourceFormat(
        String.format(
          "Cannot convert file with extension %s since we cannot find the format in our registry",
          FilenameUtils.getExtension(inputFileName)
        )
      );
    }

    // Convert...
    converter.convert(inputFile)
      .as(sourceFormat)
      .to(outputStream)
      .as(targetFormat)
      .execute();

    return outputStream;
  }
}
