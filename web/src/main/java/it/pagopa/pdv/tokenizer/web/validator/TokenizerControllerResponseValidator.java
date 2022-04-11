package it.pagopa.pdv.tokenizer.web.validator;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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


    @AfterReturning(pointcut = "controllersPointcut()", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        log.trace("validateResponse");
        log.debug("result = {}", result);

        if (result != null) {
            if (Collection.class.isAssignableFrom(result.getClass())) {
                ((Collection<?>) result).forEach(this::validate);
            } else {
                validate(result);
            }
        }
    }

    private void validate(Object result) {
        Set<ConstraintViolation<Object>> validationResults = validator.validate(result);
        if (validationResults.size() > 0) {
            Map<String, List<String>> errorMessage = new HashMap<>();
            validationResults.forEach((error) -> {
                String fieldName = error.getPropertyPath().toString();
                errorMessage.computeIfAbsent(fieldName, s -> new ArrayList<>())
                        .add(error.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " constraint violation");
            });
            throw new RuntimeException(errorMessage.toString());
        }
    }


    @Pointcut("execution(* it.pagopa.pdv.tokenizer.web.controller.*.*(..))")
    public void controllersPointcut() {
        // Do nothing because is a pointcut
    }

}
