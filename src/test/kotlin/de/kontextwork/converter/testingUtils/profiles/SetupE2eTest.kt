package de.kontextwork.converter.testingUtils.profiles

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * use that for full stack tests against real *SQL/mongodb databases with prefilled test DBs
 * dwtest1 / dwtest 2
 */
@Suppress("unused")
@Tag("e2e")
@ActiveProfiles("e2e")
@ExtendWith(SpringExtension::class)
@ExtendWith(
    MockitoExtension::class
)
@Retention(AnnotationRetention.RUNTIME)
annotation class SetupE2eTest 
