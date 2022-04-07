package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;

public interface TokenizerService {

    TokenDto save(String pii, Namespace namespace);

    String findById(String pii, Namespace namespace);

    String findPiiByToken(String token);

}
