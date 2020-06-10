package de.kontextwork.converter.testingUtils.slices;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Use this test slice for a full spring boot test bootstrap, including database, controllers and everything else
 */
@SuppressWarnings("unused")
@SpringBootTest
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomSpringBootTestSlice
{

}
