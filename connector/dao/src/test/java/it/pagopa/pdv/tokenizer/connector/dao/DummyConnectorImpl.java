package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;


public class DummyConnectorImpl {
    public void notThrowingException() {

    }

    public void throwingProvisionedThroughputExceededException() {
        throw new ProvisionedThroughputExceededException("ProvisionedThroughputExceededException");
    }

    public void throwingGenericException() throws Exception {
        throw new Exception("GenericException");
    }

    public void throwingAmazonDynamoDBException() {
        throw new AmazonDynamoDBException("AmazonDynamoDBException");
    }

}
