package it.pagopa.pdv.tokenizer.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalTime;

@Slf4j
@Configuration
class SwaggerConfig {

    @Configuration
    @Profile("swaggerIT")
    @PropertySource("classpath:/swagger/swagger_it.properties")
    public static class itConfig {
    }

    @Configuration
    @Profile("swaggerEN")
    @PropertySource("classpath:/swagger/swagger_en.properties")
    public static class enConfig {
    }

    private final Environment environment;


    @Autowired
    SwaggerConfig(Environment environment) {
        log.trace("Initializing {}", SwaggerConfig.class.getSimpleName());
        Assert.notNull(environment, "Environment is required");
        this.environment = environment;
    }


    @Bean
    public Docket swaggerSpringPlugin() {
        return (new Docket(DocumentationType.OAS_30))
                .apiInfo(new ApiInfoBuilder()
                        .title(environment.getProperty("swagger.title", environment.getProperty("spring.application.name")))
                        .description(environment.getProperty("swagger.description", "Api and Models"))
                        .version(environment.getProperty("swagger.version", environment.getProperty("spring.application.version")))
                        .build())
                .select().apis(RequestHandlerSelectors.basePackage("it.pagopa.pdv.tokenizer.web.controller")).build()
                .tags(new Tag("token", environment.getProperty("swagger.tag.token.description")))
                .directModelSubstitute(LocalTime.class, String.class)
                .forCodeGeneration(true);
    }

}
