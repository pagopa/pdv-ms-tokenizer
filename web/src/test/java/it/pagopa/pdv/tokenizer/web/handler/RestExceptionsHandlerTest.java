package it.pagopa.pdv.tokenizer.web.handler;

import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestExceptionsHandlerTest {

    private static final String DETAIL_MESSAGE = "detail message";

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
        Problem resource = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpMediaTypeNotAcceptableException() {
        // given
        HttpMediaTypeNotAcceptableException exceptionMock = Mockito.mock(HttpMediaTypeNotAcceptableException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        Problem resource = handler.handleHttpMediaTypeNotAcceptableException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleHttpRequestMethodNotSupportedException() {
        // given
        HttpRequestMethodNotSupportedException exceptionMock = Mockito.mock(HttpRequestMethodNotSupportedException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        Problem resource = handler.handleHttpRequestMethodNotSupportedException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @ParameterizedTest
    @ValueSource(classes = {
            ValidationException.class,
            BindException.class,
            ServletException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    void handleBadRequestException(Class<?> clazz) {
        // given
        Exception exceptionMock = (Exception) Mockito.mock(clazz);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        Problem resource = handler.handleBadRequestException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals(DETAIL_MESSAGE, resource.getMessage());
    }


    @Test
    void handleMethodArgumentNotValidException() {
        // given
        MethodArgumentNotValidException exceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn("{}");
        // when
        Problem resource = handler.handleMethodArgumentNotValidException(exceptionMock);
        // then
        assertNotNull(resource);
        assertEquals("{}", resource.getMessage());
    }


    @Test
    void handleResourceNotFoundException() {
        // given
        ResourceNotFoundException mockException = Mockito.mock(ResourceNotFoundException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        Problem response = handler.handleResourceNotFoundException(mockException);
        // then
        assertNotNull(response);
        assertEquals(DETAIL_MESSAGE, response.getMessage());
    }

}