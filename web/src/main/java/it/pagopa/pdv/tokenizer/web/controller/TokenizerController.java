package it.pagopa.pdv.tokenizer.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.TokenizerService;
import it.pagopa.pdv.tokenizer.web.model.PiiResource;
import it.pagopa.pdv.tokenizer.web.model.TokenResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static it.pagopa.pdv.tokenizer.core.logging.LogUtils.CONFIDENTIAL_MARKER;

@Slf4j
@RestController
@RequestMapping(value = "tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "token")
public class TokenizerController {

    private static final String NAMESPACE_HEADER_NAME = "x-pagopa-namespace";

    private final TokenizerService tokenizerService;


    @Autowired
    public TokenizerController(TokenizerService tokenizerService) {
        log.trace("Initializing {}", TokenizerController.class.getSimpleName());
        this.tokenizerService = tokenizerService;
    }


    @ApiOperation(value = "${swagger.api.tokens.save.summary}",
            notes = "${swagger.api.tokens.save.notes}")
    @PutMapping(value = "")
    public TokenResource save(@ApiParam("${swagger.model.namespace}")
                              @RequestHeader(NAMESPACE_HEADER_NAME)
                                      String namespace,
                              @RequestBody
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


    @ApiOperation(value = "${swagger.api.tokens.search.summary}",
            notes = "${swagger.api.tokens.search.notes}")
    @PostMapping(value = "search")
    public TokenResource search(@ApiParam("${swagger.model.namespace}")
                                    @RequestHeader(NAMESPACE_HEADER_NAME)
                                        String namespace,
                                @RequestBody
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


    @ApiOperation(value = "${swagger.api.tokens.findPii.summary}",
            notes = "${swagger.api.tokens.findPii.notes}")
    @GetMapping(value = "{token}/pii")
    public PiiResource findPii(@ApiParam("${swagger.model.token}")
                               @PathVariable UUID token) {
        log.trace("[findPii] start");
        log.debug("[findPii] inputs: token = {}", token);
        String pii = tokenizerService.findPiiByToken(token.toString());
        PiiResource piiResource = new PiiResource();
        piiResource.setPii(pii);
        log.debug(CONFIDENTIAL_MARKER, "[findPii] output = {}", piiResource);
        log.trace("[findPii] end");
        return piiResource;
    }

}
