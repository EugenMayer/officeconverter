package de.kontextwork.converter.testingUtils.slices

import org.junit.jupiter.api.Tag
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.core.annotation.AliasFor
import kotlin.reflect.KClass

/**
 * Use this test slice for a full spring boot test bootstrap, including database, controllers and everything else
 */
@WebMvcTest
@AutoConfigureCache
@Retention(AnnotationRetention.RUNTIME)
@Tag("controller-test")
@Target(allowedTargets = [AnnotationTarget.CLASS])
annotation class CustomWebMvcSlice(
    @get:AliasFor(annotation = WebMvcTest::class, attribute = "value") val value: Array<KClass<*>> = [],
    @get:AliasFor(annotation = WebMvcTest::class, attribute = "controllers") val controllers: Array<KClass<*>> = []
) {
}
