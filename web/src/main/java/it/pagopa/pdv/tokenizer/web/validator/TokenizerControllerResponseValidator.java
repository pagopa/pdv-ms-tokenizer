package it.pagopa.pdv.tokenizer.web.validator;

import it.pagopa.pdv.tokenizer.web.exception.ResponseValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

@Slf4j
@Aspect
@Component
public class TokenizerControllerResponseValidator {

    private final Validator validator;


    public TokenizerControllerResponseValidator(Validator validator) {
        log.trace("Initializing {}", TokenizerControllerResponseValidator.class.getSimpleName());
        Assert.notNull(validator, "Validator is required");
        this.validator = validator;
    }


    @Around("controllersPointcut()")
    public Mono<Object> validateResponse(ProceedingJoinPoint joinPoint) throws Throwable{
        log.trace("[validateResponse] start");
        // In the Tokenizer microservice, only a Mono<Object> can be returned by Controller methods
        // so in this case we can map the result directly to the Mono publisher.
        Mono<Object> result = (Mono<Object>) joinPoint.proceed();
        return result.map(returnValue -> {
            validate(returnValue);
            return returnValue;
        });
    }

    private void validate(Object result) {
        Set<ConstraintViolation<Object>> validationResults = validator.validate(result);
        if (!validationResults.isEmpty()) {
            Map<String, List<String>> errorMessage = new HashMap<>();
            validationResults.forEach(error -> {
                String fieldName = error.getPropertyPath().toString();
                errorMessage.computeIfAbsent(fieldName, s -> new ArrayList<>())
                        .add(error.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " constraint violation");
            });
            throw new ResponseValidationException(errorMessage.toString());
        }
    }


    @Pointcut("execution(* it.pagopa.pdv.tokenizer.web.controller.*.*(..))")
    public void controllersPointcut() {
        // Do nothing because is a pointcut
    }

}
