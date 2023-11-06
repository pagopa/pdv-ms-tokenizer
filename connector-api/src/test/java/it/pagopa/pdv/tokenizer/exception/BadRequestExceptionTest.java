package it.pagopa.pdv.tokenizer.exception;

import it.pagopa.pdv.tokenizer.connector.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadRequestExceptionTest {
    @Test
    public void testConstructorWithCause() {
        Exception cause = new Exception("Some cause");

        BadRequestException exception = new BadRequestException(cause);

        assertEquals(cause, exception.getCause(), "Cause should be set correctly.");

    }
}
