package de.kontextwork.converter.testingUtils.profiles

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * use this for integration tests and most probably combine this with a test-slice
 */
@Tag("it")
@ActiveProfiles("it")
@ExtendWith(SpringExtension::class)
@ExtendWith(MockitoExtension::class)
@Import
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class SetupItTest 
