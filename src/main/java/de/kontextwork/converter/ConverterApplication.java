package de.kontextwork.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

@SpringBootApplication
public class ConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConverterApplication.class, args);
    }

    @Configuration
    @EnableSwagger2
    public class SwaggerConfig {
        @Bean
        @Profile("development")
        public Docket developmentApi() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .useDefaultResponseMessages(false)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("de.kontextwork.converter"))
                    .paths(PathSelectors.regex("/conversion/.*"))
                    .build()
                    .apiInfo(apiInfo());
        }

        @Bean
        @Profile("production")
        public Docket productionApi() {
            return this.developmentApi().enable(false);
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
}
