package de.kontextwork.converter.testingUtils.slices;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Use this test slice for a full spring boot test bootstrap, including database, controllers and everything else
 */
@SpringBootTest(
  properties = {
    "logging.level.org.springframework.web=TRACE"
  }
)
@AutoConfigureCache
@AutoConfigureMockMvc
@ImportAutoConfiguration
@Retention(RetentionPolicy.RUNTIME)
@Tag("controller-test")
public @interface CustomWebMvcWithJpaTestSlice
{

}
