package it.pagopa.pdv.tokenizer.web.handler;

import it.pagopa.pdv.tokenizer.connector.exception.TooManyRequestsException;
import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import it.pagopa.pdv.tokenizer.web.model.Problem;
import it.pagopa.pdv.tokenizer.web.model.mapper.ProblemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.ValidationException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class RestExceptionsHandler {

    static final String UNHANDLED_EXCEPTION = "unhandled exception: ";


    public RestExceptionsHandler() {
        log.trace("Initializing {}", RestExceptionsHandler.class.getSimpleName());
    }


    @ExceptionHandler({Exception.class})
    ResponseEntity<Problem> handleThrowable(Throwable e, ServerWebExchange exchange) {
        log.error(UNHANDLED_EXCEPTION, e);
        Problem problem = new Problem(INTERNAL_SERVER_ERROR, e.getMessage());
        problem.setInstance(exchange.getRequest().getURI().getPath());
        return ProblemMapper.toResponseEntity(problem);
    }

      @ExceptionHandler({NotAcceptableStatusException.class})
      ResponseEntity<Problem> handleNotAcceptableStatusException(Exception e) {
            log.warn(e.toString());
            return ProblemMapper.toResponseEntity(new Problem(NOT_ACCEPTABLE, e.getMessage()));
        }



      @ExceptionHandler({MethodNotAllowedException.class})
      ResponseEntity<Problem> handleMethodNotAllowedException(Exception e) {
          log.warn(e.toString());
          return ProblemMapper.toResponseEntity(new Problem(METHOD_NOT_ALLOWED, e.getMessage()));
      }


    @ExceptionHandler({
            ValidationException.class,
            BindException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    ResponseEntity<Problem> handleBadRequestException(Exception e, ServerWebExchange exchange) {
        log.warn(e.toString());
        Problem problem = new Problem(BAD_REQUEST, e.getMessage());
        problem.setInstance(exchange.getRequest().getURI().getPath());
        return ProblemMapper.toResponseEntity(problem);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ServerWebExchange exchange) {
        final Problem problem = new Problem(BAD_REQUEST, "Validation failed");
        problem.setInstance(exchange.getRequest().getURI().getPath());
        problem.setInvalidParams(e.getFieldErrors().stream()
                .map(fieldError -> new Problem.InvalidParam(fieldError.getObjectName() + "." + fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList()));
        log.warn(e.toString());
        return ProblemMapper.toResponseEntity(problem);
    }


    @ExceptionHandler({ResourceNotFoundException.class})
    ResponseEntity<Problem> handleResourceNotFoundException(ResourceNotFoundException e, ServerWebExchange exchange) {
        log.warn(e.toString());
        Problem problem = new Problem(NOT_FOUND, e.getMessage());
        problem.setInstance(exchange.getRequest().getURI().getPath());
        return ProblemMapper.toResponseEntity(problem);
    }

    @ExceptionHandler({TooManyRequestsException.class})
    ResponseEntity<Problem> handleTooManyRequestException(TooManyRequestsException e) {
        log.warn(e.toString());
        return ProblemMapper.toResponseEntity(new Problem(TOO_MANY_REQUESTS, e.getMessage()));
    }

}