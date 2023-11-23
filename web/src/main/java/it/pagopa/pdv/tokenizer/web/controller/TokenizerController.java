package it.pagopa.pdv.tokenizer.web.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.TokenizerService;
import it.pagopa.pdv.tokenizer.web.annotations.CommonApiResponsesWrapper;
import it.pagopa.pdv.tokenizer.web.model.PiiResource;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import it.pagopa.pdv.tokenizer.web.model.TokenResource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static it.pagopa.pdv.tokenizer.core.logging.LogUtils.CONFIDENTIAL_MARKER;

@Slf4j
@RestController
@RequestMapping(value = "tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "token")
@XRayEnabled
public class TokenizerController {

    private static final String NAMESPACE_HEADER_NAME = "x-pagopa-namespace";

    private final TokenizerService tokenizerService;


    @Autowired
    public TokenizerController(TokenizerService tokenizerService) {
        log.trace("Initializing {}", TokenizerController.class.getSimpleName());
        this.tokenizerService = tokenizerService;
    }


    @Operation(summary = "${swagger.api.tokens.save.summary}",
            description = "${swagger.api.tokens.save.notes}")
    @CommonApiResponsesWrapper
    @PutMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public TokenResource save(@Parameter(description = "${swagger.model.namespace}")
                              @RequestHeader(NAMESPACE_HEADER_NAME)
                                      String namespace,
                              @RequestBody
                              @Valid
                                      PiiResource request) {
        log.trace("[save] start");
        log.debug(CONFIDENTIAL_MARKER, "[save] inputs: namespace = {}, request = {}", namespace, request);
        TokenDto tokenDto = tokenizerService.save(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(tokenDto.getToken()));
        tokenResource.setRootToken(UUID.fromString(tokenDto.getRootToken()));
        log.debug("[save] output = {}", tokenResource);
        log.trace("[save] end");
        return tokenResource;
    }


    @Operation(summary = "${swagger.api.tokens.search.summary}",
            description = "${swagger.api.tokens.search.notes}")
    @CommonApiResponsesWrapper
    @ApiResponse(responseCode = "404",
            description = "Not Found",
            content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = Problem.class))
            })
    @PostMapping(value = "search")
    @ResponseStatus(HttpStatus.OK)
    public TokenResource search(@Parameter(description = "${swagger.model.namespace}")
                                @RequestHeader(NAMESPACE_HEADER_NAME)
                                        String namespace,
                                @RequestBody
                                @Valid
                                        PiiResource request) {
        log.trace("[search] start");
        log.debug(CONFIDENTIAL_MARKER, "[search] inputs: namespace = {}, request = {}", namespace, request);
        TokenDto tokenDto = tokenizerService.findById(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(tokenDto.getToken()));
        tokenResource.setRootToken(UUID.fromString(tokenDto.getRootToken()));
        log.debug("[search] output = {}", tokenResource);
        log.trace("[search] end");
        return tokenResource;
    }


    @Operation(summary = "${swagger.api.tokens.findPii.summary}",
            description = "${swagger.api.tokens.findPii.notes}")
    @CommonApiResponsesWrapper
    @ApiResponse(responseCode = "404",
            description = "Not Found",
            content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = Problem.class))
            })
    @GetMapping(value = "{token}/pii")
    @ResponseStatus(HttpStatus.OK)
    public PiiResource findPii(@Parameter(description = "${swagger.model.token}")
                               @PathVariable UUID token,
                               @Parameter(description = "${swagger.model.namespace}")
                               @RequestHeader(NAMESPACE_HEADER_NAME)
                               String namespace) {
        log.trace("[findPii] start");
        log.debug("[findPii] inputs: token = {}", token);
        String pii = tokenizerService.findPiiByToken(token.toString(), namespace);
        PiiResource piiResource = new PiiResource();
        piiResource.setPii(pii);
        log.debug(CONFIDENTIAL_MARKER, "[findPii] output = {}", piiResource);
        log.trace("[findPii] end");
        return piiResource;
    }

}
