package it.pagopa.pdv.tokenizer.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import it.pagopa.pdv.tokenizer.core.TokenizerService;
import it.pagopa.pdv.tokenizer.web.model.CreateTokenDto;
import it.pagopa.pdv.tokenizer.web.model.FilterCriteria;
import it.pagopa.pdv.tokenizer.web.model.PiiResource;
import it.pagopa.pdv.tokenizer.web.model.TokenResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "tokens")
public class TokenizerController {

    private final TokenizerService tokenizerService;


    @Autowired
    public TokenizerController(TokenizerService tokenizerService) {
        this.tokenizerService = tokenizerService;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.save.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.save.notes}")
    @PutMapping(value = "")
    public TokenResource save(@ApiParam("${swagger.ms-tokenizer.token.model.namespace}")
                              @RequestHeader
                                      Namespace namespace,
                              @RequestBody
                                      CreateTokenDto request) {
        String token = tokenizerService.save(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(token));
        return tokenResource;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.notes}")
    @GetMapping(value = "search")
    public TokenResource searchToken(@ApiParam("${swagger.ms-tokenizer.token.model.namespace}")
                                     @RequestHeader
                                             Namespace namespace,
                                     @RequestBody
                                             FilterCriteria request) {
        String token = tokenizerService.findById(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(token));
        return tokenResource;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.notes}")
    @GetMapping(value = "{token}/pii")
    public PiiResource findPii(@ApiParam("${swagger.ms-tokenizer.token.model.namespace}")
                               @RequestHeader
                                       Namespace namespace,
                               @ApiParam("${swagger.tokenizer.token.model.token}")
                               @PathVariable UUID token) {
        String pii = tokenizerService.findPiiByToken(token.toString());
        PiiResource piiResource = new PiiResource();
        piiResource.setPii(pii);
        return piiResource;
    }

}
