package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;


public class TokenizerConnectorImplDummy {
    public void notThrowingProvisionedThroughputExceededException() {

    }

    public void throwingProvisionedThroughputExceededException() {
        throw new ProvisionedThroughputExceededException("ProvisionedThroughputExceededException");
    }

    public void throwingGenericException() throws Exception {
        throw new Exception("GenericException");
    }

}
