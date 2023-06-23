package it.pagopa.pdv.tokenizer.connector.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(Throwable cause) {
        super(cause);
    }
}

