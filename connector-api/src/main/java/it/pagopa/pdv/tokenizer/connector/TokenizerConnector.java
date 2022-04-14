package it.pagopa.pdv.tokenizer.connector;

import it.pagopa.pdv.tokenizer.connector.model.TokenDto;

import java.util.Optional;

public interface TokenizerConnector {

    TokenDto save(String pii, String namespace);

    Optional<TokenDto> findById(String pii, String namespace);

    Optional<String> findPiiByToken(String token);

}
