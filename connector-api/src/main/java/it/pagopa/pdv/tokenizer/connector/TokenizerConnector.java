package it.pagopa.pdv.tokenizer.connector;

import it.pagopa.pdv.tokenizer.connector.model.Namespace;

public interface TokenizerConnector {

    String save(String pii, Namespace namespace);

    String findById(String pii, Namespace namespace);

    String findPiiByToken(String token);

}
