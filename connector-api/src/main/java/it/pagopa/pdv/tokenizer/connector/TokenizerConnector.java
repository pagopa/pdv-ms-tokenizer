package it.pagopa.pdv.tokenizer.connector;

import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;

public interface TokenizerConnector {

    TokenDto save(String pii, Namespace namespace);

    String findById(String pii, Namespace namespace);

    String findPiiByToken(String token);

}
