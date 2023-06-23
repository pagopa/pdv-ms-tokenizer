package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.model.TokenDto;

public interface TokenizerService {

    TokenDto save(String pii, String namespace);

    TokenDto findById(String pii, String namespace);

    String findPiiByToken(String token, String namespace);

}
