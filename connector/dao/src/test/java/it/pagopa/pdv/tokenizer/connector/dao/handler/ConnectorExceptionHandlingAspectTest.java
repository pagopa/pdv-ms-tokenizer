package it.pagopa.pdv.tokenizer.connector.dao.handler;

import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImplDummy;
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
        TokenizerConnectorImplDummy.class,
        ConnectorExceptionHandlingAspect.class})
@EnableAspectJAutoProxy
public class ConnectorExceptionHandlingAspectTest {
    @Autowired
    private TokenizerConnectorImplDummy connector;

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
        assertDoesNotThrow(() -> connector.notThrowingProvisionedThroughputExceededException());
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
}
