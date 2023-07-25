package it.pagopa.pdv.tokenizer.web.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = {
                                @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                        schema = @Schema(implementation = Problem.class))
                        }
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content = {
                                @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                        schema = @Schema(implementation = Problem.class))
                        }
                ),
                @ApiResponse(
                        responseCode = "429",
                        description = "Too Many Requests",
                        content = {
                                @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                        schema = @Schema(implementation = Problem.class))
                        }
                )
        }
)
public @interface CommonApiResponsesWrapper {
}
