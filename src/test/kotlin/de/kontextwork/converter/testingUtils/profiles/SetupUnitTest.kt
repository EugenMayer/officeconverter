package de.kontextwork.converter.testingUtils.profiles

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

/**
 * Pure unit tests, no spring bootstrap - fast any easy
 */
@Tag("unit")
@ActiveProfiles("unit")
@ExtendWith(MockitoExtension::class)
@Retention(AnnotationRetention.RUNTIME)
annotation class SetupUnitTest 
