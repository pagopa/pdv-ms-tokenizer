package it.pagopa.pdv.tokenizer.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

/**
 * The Class SwaggerConfig.
 */
@Slf4j
@Configuration
class SwaggerConfig {

    public static final String AUTH_SCHEMA_NAME = "bearerAuth";

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
                .select().apis(RequestHandlerSelectors.basePackage("it.pagopa.pdv.tokenizer.web.controller")).build()
                .directModelSubstitute(LocalTime.class, String.class);
    }

//    @Bean
//    public Docket swaggerSpringPlugin() {
//        return (new Docket(DocumentationType.OAS_30))
//                .apiInfo(new ApiInfoBuilder()
//                        .title(environment.getProperty("swagger.title", environment.getProperty("spring.application.name")))
//                        .description(environment.getProperty("swagger.description", "Api and Models"))
//                        .version(environment.getProperty("swagger.version", environment.getProperty("spring.application.version")))
//                        .build())
//                .select().apis(RequestHandlerSelectors.basePackage("it.pagopa.pdv.tokenizer.web.controller")).build()
//                .tags(new Tag("users", environment.getProperty("swagger.users.api.description")))//TODO change Name
//                .directModelSubstitute(LocalTime.class, String.class)
//                .securityContexts(Collections.singletonList(SecurityContext.builder()
//                        .securityReferences(defaultAuth())
//                        .build()))
//                .securitySchemes(Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER
//                        .name(AUTH_SCHEMA_NAME)
//                        .description(environment.getProperty("swagger.security.schema.bearer.description"))
//                        .build()));
//    }


    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference(AUTH_SCHEMA_NAME, authorizationScopes));
    }

}
