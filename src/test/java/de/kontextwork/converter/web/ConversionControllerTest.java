package de.kontextwork.converter.web;

import de.kontextwork.converter.testingUtils.profiles.SetupItTest;
import de.kontextwork.converter.testingUtils.slices.CustomWebMvcWithJpaTestSlice;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SetupItTest
@CustomWebMvcWithJpaTestSlice
class ConversionControllerTest
{
  @Value("classpath:testfiles/withpictures.docx")
  private Resource testDocx;

  final private String requestUrl = "/conversion";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void convertWorks() throws Exception
  {
    var tika = new Tika();
    Resource testResource = testDocx;

    String mimeType = tika.detect(testResource.getFile());
    var testFile = new MockMultipartFile("file", testResource.getFilename(), mimeType, testResource.getInputStream());

    var targetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.getFilename()), "html");
    var targetMimeType = tika.detect(targetFilename); // should be html obviously

    mockMvc
      .perform(
        multipart(requestUrl)
          .file(testFile)
          .param("format", "html")
      )
      .andExpect(status().isOk())
      .andExpect(header().string("Content-Disposition", "attachment; filename=" + targetFilename))
      .andExpect(content().contentType(targetMimeType));
  }

  @Test
  void convertToHtmlEmbedsImages() throws Exception
  {
    var tika = new Tika();
    Resource testResource = testDocx;

    String mimeType = tika.detect(testResource.getFile());
    var testFile = new MockMultipartFile("file", testResource.getFilename(), mimeType, testResource.getInputStream());

    var targetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.getFilename()), "html");
    var targetMimeType = tika.detect(targetFilename); // should be html obviously

    mockMvc
      .perform(
        multipart(requestUrl)
          .file(testFile)
          .param("format", "html")
      )
      .andExpect(status().isOk())
      .andExpect(header().string("Content-Disposition", "attachment; filename=" + targetFilename))
      .andExpect(content().contentType(targetMimeType))
      .andExpect(content().string(containsString("<img src=\"data:image/")));
  }
}
