package de.kontextwork.converter.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@Profile(Profiles.DEV)
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("de.kontextwork.converter"))
                .paths(PathSelectors.regex("/conversion/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Conversion REST API",
                "Conversion REST API for Online conversion. Automates conversions between office document formats using JODconverter, LibreOffice or Apache OpenOffice.",
                "0.1",
                "Terms of service",
                new Contact("No", "kontextwork.de", "no@kontextwork.de"),
                "Unknown",
                "Unknown",
                Collections.emptyList());
    }
}