package de.kontextwork.converter.base

import de.kontextwork.converter.testingUtils.profiles.SetupUnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks

@SetupUnitTest
class FileNameUtilsTest {
    @InjectMocks
    lateinit var underTest: FileNameUtils;

    @Test
    fun shouldStripFolder() {
        assertThat(underTest.extractFilenameOnly("../../foo.ext")).isEqualTo("foo.ext")
    }

    @Test
    fun shouldAllowFileName() {
        assertThat(underTest.extractFilenameOnly("foo.ext")).isEqualTo("foo.ext")
    }

    @Test
    fun shouldAllowFileNameWithSpaces() {
        assertThat(underTest.extractFilenameOnly("foo bar.ext")).isEqualTo("foo bar.ext")
    }
}
