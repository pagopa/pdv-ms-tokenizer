package it.pagopa.pdv.tokenizer.web.config;

import com.fasterxml.classmate.TypeResolver;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.Response;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Configuration
class SwaggerConfig {

    public static final Response INTERNAL_SERVER_ERROR_RESPONSE = new ResponseBuilder()
            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .description(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .representation(MediaType.APPLICATION_PROBLEM_JSON).apply(repBuilder ->
                    repBuilder.model(modelSpecBuilder ->
                            modelSpecBuilder.referenceModel(refModelSpecBuilder ->
                                    refModelSpecBuilder.key(modelKeyBuilder ->
                                            modelKeyBuilder.qualifiedModelName(qualifiedModelNameBuilder ->
                                                    qualifiedModelNameBuilder.namespace(Problem.class.getPackageName())
                                                            .name(Problem.class.getSimpleName()))))))
            .build();
    private static final Response BAD_REQUEST_RESPONSE = new ResponseBuilder()
            .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .description(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .representation(MediaType.APPLICATION_PROBLEM_JSON).apply(repBuilder ->
                    repBuilder.model(modelSpecBuilder ->
                            modelSpecBuilder.referenceModel(refModelSpecBuilder ->
                                    refModelSpecBuilder.key(modelKeyBuilder ->
                                            modelKeyBuilder.qualifiedModelName(qualifiedModelNameBuilder ->
                                                    qualifiedModelNameBuilder.namespace(Problem.class.getPackageName())
                                                            .name(Problem.class.getSimpleName()))))))
            .build();
    private static final Response NOT_FOUND_RESPONSE = new ResponseBuilder()
            .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .description(HttpStatus.NOT_FOUND.getReasonPhrase())
            .representation(MediaType.APPLICATION_PROBLEM_JSON).apply(repBuilder ->
                    repBuilder.model(modelSpecBuilder ->
                            modelSpecBuilder.referenceModel(refModelSpecBuilder ->
                                    refModelSpecBuilder.key(modelKeyBuilder ->
                                            modelKeyBuilder.qualifiedModelName(qualifiedModelNameBuilder ->
                                                    qualifiedModelNameBuilder.namespace(Problem.class.getPackageName())
                                                            .name(Problem.class.getSimpleName()))))))
            .build();

    private static final Response TOO_MANY_REQUESTS_RESPONSE = new ResponseBuilder()
            .code(String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()))
            .description(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
            .representation(MediaType.APPLICATION_PROBLEM_JSON).apply(repBuilder ->
                    repBuilder.model(modelSpecBuilder ->
                            modelSpecBuilder.referenceModel(refModelSpecBuilder ->
                                    refModelSpecBuilder.key(modelKeyBuilder ->
                                            modelKeyBuilder.qualifiedModelName(qualifiedModelNameBuilder ->
                                                    qualifiedModelNameBuilder.namespace(Problem.class.getPackageName()).name(Problem.class.getSimpleName()))))))
            .build();

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
    public Docket swaggerSpringPlugin(@Autowired TypeResolver typeResolver) {
        return (new Docket(DocumentationType.OAS_30))
                .apiInfo(new ApiInfoBuilder()
                        .title(environment.getProperty("swagger.title", environment.getProperty("spring.application.name")))
                        .description(environment.getProperty("swagger.description", "Api and Models"))
                        .version(environment.getProperty("swagger.version", environment.getProperty("spring.application.version")))
                        .build())
                .select().apis(RequestHandlerSelectors.basePackage("it.pagopa.pdv.tokenizer.web.controller")).build()
                .tags(new Tag("token", environment.getProperty("swagger.tag.token.description")))
                .directModelSubstitute(LocalTime.class, String.class)
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.GET, List.of(INTERNAL_SERVER_ERROR_RESPONSE, BAD_REQUEST_RESPONSE, NOT_FOUND_RESPONSE, TOO_MANY_REQUESTS_RESPONSE))
                .globalResponses(HttpMethod.DELETE, List.of(INTERNAL_SERVER_ERROR_RESPONSE, BAD_REQUEST_RESPONSE, TOO_MANY_REQUESTS_RESPONSE))
                .globalResponses(HttpMethod.POST, List.of(INTERNAL_SERVER_ERROR_RESPONSE, BAD_REQUEST_RESPONSE, TOO_MANY_REQUESTS_RESPONSE))
                .globalResponses(HttpMethod.PUT, List.of(INTERNAL_SERVER_ERROR_RESPONSE, BAD_REQUEST_RESPONSE, TOO_MANY_REQUESTS_RESPONSE))
                .globalResponses(HttpMethod.PATCH, List.of(INTERNAL_SERVER_ERROR_RESPONSE, BAD_REQUEST_RESPONSE, TOO_MANY_REQUESTS_RESPONSE))
                .additionalModels(typeResolver.resolve(Problem.class))
                .forCodeGeneration(true);
    }

}
