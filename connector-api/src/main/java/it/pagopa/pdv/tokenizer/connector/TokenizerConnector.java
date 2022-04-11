package it.pagopa.pdv.tokenizer.connector;

import it.pagopa.pdv.tokenizer.connector.model.TokenDto;

public interface TokenizerConnector {

    TokenDto save(String pii, String namespace);

    String findById(String pii, String namespace);

    String findPiiByToken(String token);

}
