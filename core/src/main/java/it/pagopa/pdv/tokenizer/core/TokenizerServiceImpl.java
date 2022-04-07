package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
class TokenizerServiceImpl implements TokenizerService {

    private final TokenizerConnector tokenizerConnector;


    TokenizerServiceImpl(TokenizerConnector tokenizerConnector) {
        this.tokenizerConnector = tokenizerConnector;
    }


    @Override
    public TokenDto save(String pii, Namespace namespace) {
        Assert.hasText(pii, "A Private Data is required");
        Assert.notNull(namespace, "A Namespace is required");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "save input: pii = {}, namespace = {}", pii, namespace);
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        log.debug("save output: token = {}", tokenDto);
        return tokenDto;
    }

    @Override
    public String findById(String pii, Namespace namespace) {
        Assert.hasText(pii, "A Private Data is required");
        Assert.notNull(namespace, "A Namespace is required");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "findById input: pii = {}, namespace = {}", pii, namespace);
        String token = tokenizerConnector.findById(pii, namespace);
        log.debug("findById output: token = {}", token);
        return token;
    }

    @Override
    public String findPiiByToken(String token) {
        Assert.hasText(token, "A token is required");
        log.debug("findPiiByToken input: token = {}", token);
        String pii = tokenizerConnector.findPiiByToken(token);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "findById output: pii = {}", pii);
        return pii;
    }

}
