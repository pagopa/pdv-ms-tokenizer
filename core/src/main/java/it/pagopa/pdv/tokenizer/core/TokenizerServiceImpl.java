package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.core.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
class TokenizerServiceImpl implements TokenizerService {

    private final TokenizerConnector tokenizerConnector;


    TokenizerServiceImpl(TokenizerConnector tokenizerConnector) {
        log.trace("Initializing {}", TokenizerConnector.class.getSimpleName());
        this.tokenizerConnector = tokenizerConnector;
    }


    @Override
    public TokenDto save(String pii, String namespace) {
        log.trace("[save] start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "[save] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        log.debug("[save] output = {}", tokenDto);
        log.trace("[save] end");
        return tokenDto;
    }


    @Override
    public TokenDto findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        TokenDto tokenDto = tokenizerConnector.findById(pii, namespace)
                .orElseThrow(ResourceNotFoundException::new);
        log.debug("[findById] output = {}", tokenDto);
        log.trace("[findById] end");
        return tokenDto;
    }


    @Override
    public String findPiiByToken(String token) {
        log.trace("[findPiiByToken] start");
        log.debug("[findPiiByToken] inputs: token = {}", token);
        Assert.hasText(token, "A token is required");
        String pii = tokenizerConnector.findPiiByToken(token)
                .orElseThrow(ResourceNotFoundException::new);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
        log.trace("[findPiiByToken] end");
        return pii;
    }

}
