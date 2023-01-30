package de.kontextwork.converter.testingUtils.slices

import org.springframework.boot.test.context.SpringBootTest

/**
 * Use this test slice for a full spring boot test bootstrap, including database, controllers and everything else
 */
@Suppress("unused")
@SpringBootTest
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomSpringBootTestSlice 
