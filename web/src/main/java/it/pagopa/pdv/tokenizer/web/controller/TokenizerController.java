package it.pagopa.pdv.tokenizer.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
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
        log.trace("Initializing {}", TokenizerController.class.getSimpleName());
        this.tokenizerService = tokenizerService;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.save.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.save.notes}")
    @PutMapping(value = "")
    public TokenResource save(@ApiParam("${swagger.ms-tokenizer.token.model.namespace}")
                                  @RequestHeader("x-pagopa-namespace")
                                      Namespace namespace,
                              @RequestBody
                                      CreateTokenDto request) {
        TokenDto tokenDto = tokenizerService.save(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(tokenDto.getToken()));
        tokenResource.setRootToken(UUID.fromString(tokenDto.getRootToken()));
        return tokenResource;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.searchToken.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.searchToken.notes}")
    @PostMapping(value = "search")
    public TokenResource searchToken(@ApiParam("${swagger.ms-tokenizer.token.model.namespace}")
                                         @RequestHeader("x-pagopa-namespace")
                                             Namespace namespace,
                                     @RequestBody
                                             FilterCriteria request) {
        String token = tokenizerService.findById(request.getPii(), namespace);
        TokenResource tokenResource = new TokenResource();
        tokenResource.setToken(UUID.fromString(token));
        return tokenResource;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.findPii.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.findPii.notes}")
    @GetMapping(value = "{token}/pii")
    public PiiResource findPii(@ApiParam("${swagger.tokenizer.token.model.token}")
                               @PathVariable UUID token) {
        String pii = tokenizerService.findPiiByToken(token.toString());
        PiiResource piiResource = new PiiResource();
        piiResource.setPii(pii);
        return piiResource;
    }

}
