package it.pagopa.pdv.tokenizer.connector.dao.handler;

import it.pagopa.pdv.tokenizer.connector.dao.DummyConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.exception.BadRequestException;
import it.pagopa.pdv.tokenizer.connector.exception.TooManyRequestsException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {
        ValidationAutoConfiguration.class,
        DummyConnectorImpl.class,
        ConnectorExceptionHandlingAspect.class})
@EnableAspectJAutoProxy
public class ConnectorExceptionHandlingAspectTest {
    @Autowired
    private DummyConnectorImpl connector;

    @SpyBean
    private ConnectorExceptionHandlingAspect exceptionHandlingAspectSpy;

    @Test
    void handleProvisionedThroughputExceededException_throwing(){
        assertThrows(TooManyRequestsException.class, () -> connector.throwingProvisionedThroughputExceededException());
        verify(exceptionHandlingAspectSpy, Mockito.times(1))
                .handleProvisionedThroughputExceededExceptionCall(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }
    @Test
    void handleProvisionedThroughputExceededException_notThrowing(){
        assertDoesNotThrow(() -> connector.notThrowingException());
        verify(exceptionHandlingAspectSpy, Mockito.times(0))
                .handleProvisionedThroughputExceededExceptionCall(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }
    @Test
    void handleProvisionedThroughputExceededException_throwingGenericException(){
        assertThrows(Exception.class, () -> connector.throwingGenericException());
        verify(exceptionHandlingAspectSpy, Mockito.times(0))
                .handleProvisionedThroughputExceededExceptionCall(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }

    @Test
    void handleAmazonDynamoDBException_throwing(){
        assertThrows(BadRequestException.class, () -> connector.throwingAmazonDynamoDBException());
        verify(exceptionHandlingAspectSpy, Mockito.times(1))
                .handleAmazonDynamoDBException(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }

    @Test
    void handleAmazonDynamoDBException_notThrowing(){
        assertDoesNotThrow(() -> connector.notThrowingException());
        verify(exceptionHandlingAspectSpy, Mockito.times(0))
                .handleAmazonDynamoDBException(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }

    @Test
    void handleAmazonDynamoDBException_throwingGenericException(){
        assertThrows(Exception.class, () -> connector.throwingGenericException());
        verify(exceptionHandlingAspectSpy, Mockito.times(0))
                .handleAmazonDynamoDBException(any());
        verifyNoMoreInteractions(exceptionHandlingAspectSpy);
    }
}
