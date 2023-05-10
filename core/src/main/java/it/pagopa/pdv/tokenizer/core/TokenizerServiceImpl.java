package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.core.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

@Slf4j
@Service
class TokenizerServiceImpl implements TokenizerService {

    private final TokenizerConnector tokenizerConnector;


    TokenizerServiceImpl(TokenizerConnector tokenizerConnector) {
        log.trace("Initializing {}", TokenizerConnector.class.getSimpleName());
        this.tokenizerConnector = tokenizerConnector;
    }


    @Override
    public Mono<TokenDto> save(String pii, String namespace) {
        log.trace("[save] start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "[save] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        return tokenizerConnector.save(pii, namespace)
                .doOnSuccess(tokenDto -> {
                    log.debug("[save] output = {}", tokenDto);
                    log.trace("[save] end");
                });
    }


    @Override
    public Mono<TokenDto> findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        return tokenizerConnector.findById(pii, namespace)
                .switchIfEmpty(Mono.error(ResourceNotFoundException::new))
                .doOnSuccess(tokenDto -> {
                    log.debug("[findById] output = {}", tokenDto);
                    log.trace("[findById] end");
                });
    }


    @Override
    public Mono<String> findPiiByToken(String token, String namespace) {
        log.trace("[findPiiByToken] start");
        log.debug("[findPiiByToken] inputs: token = {}, namespace = {}", token, namespace);
        Assert.hasText(token, "A token is required");
        Assert.hasText(namespace, "A namespace is required");
        return tokenizerConnector.findPiiByToken(token, namespace)
                .switchIfEmpty(Mono.error(ResourceNotFoundException::new))
                .doOnSuccess(pii -> {
                    log.debug(LogUtils.CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
                    log.trace("[findPiiByToken] end");
                });
    }

}
