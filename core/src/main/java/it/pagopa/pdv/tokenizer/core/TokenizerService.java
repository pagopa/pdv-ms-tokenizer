package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.model.Namespace;

public interface TokenizerService {

    String save(String pii, Namespace namespace);

    String findById(String pii, Namespace namespace);

    String findPiiByToken(String token);

}
