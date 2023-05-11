package it.pagopa.pdv.tokenizer.web.handler;

import it.pagopa.pdv.tokenizer.connector.exception.TooManyRequestsException;
import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ValidationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;

class RestExceptionsHandlerTest {

    private static final String DETAIL_MESSAGE = "detail message";
    private static final String URL_PATH = "test/foo";
    private static final MockServerWebExchange MOCK_SERVER_WEB_EXCHANGE = MockServerWebExchange.from(MockServerHttpRequest.get(URL_PATH + "?a=5")
            .build());

    private final RestExceptionsHandler handler;


    public RestExceptionsHandlerTest() {
        this.handler = new RestExceptionsHandler();
    }


    @Test
    void handleThrowable() {
        // given
        Throwable exceptionMock = Mockito.mock(Throwable.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleThrowable(exceptionMock, MOCK_SERVER_WEB_EXCHANGE);
        // then
        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().getStatus());
        assertEquals(URL_PATH, responseEntity.getBody().getInstance());
    }


    @ParameterizedTest
    @ValueSource(classes = {
            ValidationException.class,
            BindException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    void handleBadRequestException(Class<?> clazz) {
        // given
        Exception exceptionMock = (Exception) Mockito.mock(clazz);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        String urlPath = "test/foo";
        // when
        ResponseEntity<Problem> responseEntity = handler.handleBadRequestException(exceptionMock, MOCK_SERVER_WEB_EXCHANGE);
        // then
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(BAD_REQUEST.value(), responseEntity.getBody().getStatus());
        assertEquals(URL_PATH, responseEntity.getBody().getInstance());
    }


//FIXME: ServletException is no more a dependency

//    @Test
//    void handleHttpMediaTypeNotAcceptableException() {
//        // given
//        HttpMediaTypeNotAcceptableException exceptionMock = Mockito.mock(HttpMediaTypeNotAcceptableException.class);
//        Mockito.when(exceptionMock.getMessage())
//                .thenReturn(DETAIL_MESSAGE);
//        // when
//        ResponseEntity<Problem> responseEntity = handler.handleHttpMediaTypeNotAcceptableException(exceptionMock);
//        // then
//        assertNotNull(responseEntity);
//        assertEquals(NOT_ACCEPTABLE, responseEntity.getStatusCode());
//        assertNotNull(responseEntity.getBody());
//        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
//        assertEquals(NOT_ACCEPTABLE.value(), responseEntity.getBody().getStatus());
//    }

//FIXME: ServletException is no more a dependency

//    @Test
//    void handleHttpRequestMethodNotSupportedException() {
//        // given
//        HttpRequestMethodNotSupportedException exceptionMock = Mockito.mock(HttpRequestMethodNotSupportedException.class);
//        Mockito.when(exceptionMock.getMessage())
//                .thenReturn(DETAIL_MESSAGE);
//        // when
//        ResponseEntity<Problem> responseEntity = handler.handleHttpRequestMethodNotSupportedException(exceptionMock);
//        // then
//        assertNotNull(responseEntity);
//        assertEquals(METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
//        assertNotNull(responseEntity.getBody());
//        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
//        assertEquals(METHOD_NOT_ALLOWED.value(), responseEntity.getBody().getStatus());
//    }


    @Test
    void handleMethodArgumentNotValidException() {
        // given
        MethodArgumentNotValidException exceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
        final FieldError errorMock = new FieldError("objectName", "fieldName", "is not valid");
        Mockito.when(exceptionMock.getFieldErrors())
                .thenReturn(List.of(errorMock));
        // when
        ResponseEntity<Problem> responseEntity = handler.handleMethodArgumentNotValidException(exceptionMock, MOCK_SERVER_WEB_EXCHANGE);
        // then
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Validation failed", responseEntity.getBody().getDetail());
        assertNotNull(responseEntity.getBody().getInvalidParams());
        assertEquals(1, responseEntity.getBody().getInvalidParams().size());
        assertEquals(errorMock.getObjectName() + "." + errorMock.getField(), responseEntity.getBody().getInvalidParams().get(0).getName());
        assertEquals(errorMock.getDefaultMessage(), responseEntity.getBody().getInvalidParams().get(0).getReason());
        assertEquals(BAD_REQUEST.value(), responseEntity.getBody().getStatus());
        assertEquals(URL_PATH, responseEntity.getBody().getInstance());
    }


    @Test
    void handleResourceNotFoundException() {
        // given
        ResourceNotFoundException mockException = Mockito.mock(ResourceNotFoundException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleResourceNotFoundException(mockException, MOCK_SERVER_WEB_EXCHANGE);
        // then
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(NOT_FOUND.value(), responseEntity.getBody().getStatus());
        assertEquals(URL_PATH, responseEntity.getBody().getInstance());
    }
    @Test
    void handleTooManyRequestsException(){
        // given
        TooManyRequestsException mockException = Mockito.mock(TooManyRequestsException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);

        // when
        ResponseEntity<Problem> responseEntity = handler.handleTooManyRequestException(mockException);
        // then
        assertNotNull(responseEntity);
        assertEquals(TOO_MANY_REQUESTS,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE,responseEntity.getBody().getDetail());
        assertEquals(TOO_MANY_REQUESTS.value(),responseEntity.getBody().getStatus());

    }

}