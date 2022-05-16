package it.pagopa.pdv.tokenizer.web.handler;

import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionsHandler {

    static final String UNHANDLED_EXCEPTION = "unhandled exception: ";


    public RestExceptionsHandler() {
        log.trace("Initializing {}", RestExceptionsHandler.class.getSimpleName());
    }


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Problem handleThrowable(Throwable e) {
        log.error(UNHANDLED_EXCEPTION, e);
        return new Problem(e.getMessage());
    }


    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    Problem handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.warn(e.toString());
        return new Problem(e.getMessage());
    }


    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    Problem handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn(e.toString());
        return new Problem(e.getMessage());
    }


    @ExceptionHandler({
            ValidationException.class,
            BindException.class,
            ServletException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Problem handleBadRequestException(Exception e) {
        log.warn(e.toString());
        return new Problem(e.getMessage());
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Problem handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, List<String>> errorMessage = new HashMap<>();
        e.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            errorMessage.computeIfAbsent(fieldName, s -> new ArrayList<>())
                    .add(error.getCode() + " constraint violation");
        });
        log.warn(errorMessage.toString());
        return new Problem(errorMessage.toString());
    }


    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Problem handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn(e.toString());
        return new Problem(e.getMessage());
    }

}