package de.kontextwork.converter.module.convert.controller

import de.kontextwork.converter.base.FileNameUtils
import de.kontextwork.converter.module.convert.ConverterService
import de.kontextwork.converter.testingUtils.profiles.SetupItTest
import de.kontextwork.converter.testingUtils.slices.CustomWebMvcSlice
import org.apache.commons.io.FilenameUtils
import org.apache.tika.Tika
import org.hamcrest.CoreMatchers
import org.jodconverter.boot.autoconfigure.JodConverterLocalAutoConfiguration
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Import
import org.springframework.core.io.Resource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SetupItTest
@CustomWebMvcSlice
@Import(value = [ConverterService::class, FileNameUtils::class, JodConverterLocalAutoConfiguration::class])
internal class ConversionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Value("classpath:testfiles/withpictures.docx")
    private val testDocx: Resource? = null

    @Value("classpath:testfiles/template.dotx")
    private val testDotx: Resource? = null

    @Value("classpath:testfiles/template.xltx")
    private val testXltx: Resource? = null

    private val requestUrl = "/conversion"

    @Test
    @DisplayName("Should convert docx to html")
    @Throws(Exception::class)
    fun convertWorks() {
        // setup
        val tika = Tika()
        val testResource = testDocx
        val mimeType = tika.detect(testResource!!.file)
        val testFile = MockMultipartFile("file", testResource.filename, mimeType, testResource.inputStream)
        val htmlTargetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.filename), "html")

        // run / verify
        mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(requestUrl)
                    .file(testFile)
                    .param("format", "html")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=$htmlTargetFilename")
            )
            .andExpect(MockMvcResultMatchers.content().contentType(tika.detect(htmlTargetFilename)))
    }

    @Test
    @DisplayName("Should convert dotx to html")
    @Throws(Exception::class)
    fun convertDotxWorks() {
        // setup
        val tika = Tika()
        val testResource = testDotx
        val mimeType = tika.detect(testResource!!.file)
        val testFile = MockMultipartFile("file", testResource.filename, mimeType, testResource.inputStream)
        val htmlTargetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.filename), "html")

        // run / verify
        mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(requestUrl)
                    .file(testFile)
                    .param("format", "html")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=$htmlTargetFilename")
            )
            .andExpect(MockMvcResultMatchers.content().contentType(tika.detect(htmlTargetFilename)))
    }

    @Test
    @DisplayName("Should convert xltx to html")
    @Throws(Exception::class)
    fun convertXltxWorks() {
        // setup
        val tika = Tika()
        val testResource = testXltx
        val mimeType = tika.detect(testResource!!.file)
        val testFile = MockMultipartFile("file", testResource.filename, mimeType, testResource.inputStream)
        val htmlTargetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.filename), "html")

        // run / verify
        mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(requestUrl)
                    .file(testFile)
                    .param("format", "html")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=$htmlTargetFilename")
            )
            .andExpect(MockMvcResultMatchers.content().contentType(tika.detect(htmlTargetFilename)))
    }

    @Test
    @DisplayName("Should convert docx to html using embedded images")
    @Throws(Exception::class)
    fun convertToHtmlEmbedsImages() {
        // setup
        val tika = Tika()
        val testResource = testDocx
        val mimeType = tika.detect(testResource!!.file)
        val testFile = MockMultipartFile("file", testResource.filename, mimeType, testResource.inputStream)
        val targetFilename = String.format("%s.%s", FilenameUtils.getBaseName(testResource.filename), "html")
        val targetMimeType = tika.detect(targetFilename) // should be html obviously

        // run / verify
        mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(requestUrl)
                    .file(testFile)
                    .param("format", "html")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=$targetFilename")
            )
            .andExpect(MockMvcResultMatchers.content().contentType(targetMimeType))
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("<img src=\"data:image/")))
    }
}
