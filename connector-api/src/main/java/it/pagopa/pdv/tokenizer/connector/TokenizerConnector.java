package it.pagopa.pdv.tokenizer.connector;

import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import reactor.core.publisher.Mono;

public interface TokenizerConnector {

    Mono<TokenDto> save(String pii, String namespace);

    Mono<TokenDto> findById(String pii, String namespace);

    Mono<String> findPiiByToken(String token, String namespace);

}
