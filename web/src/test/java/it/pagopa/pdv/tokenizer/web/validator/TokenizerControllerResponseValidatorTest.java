package it.pagopa.pdv.tokenizer.web.validator;

import it.pagopa.pdv.tokenizer.web.controller.DummyController;
import it.pagopa.pdv.tokenizer.web.exception.ResponseValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@SpringBootTest(classes = {
        ValidationAutoConfiguration.class,
        DummyController.class,
        TokenizerControllerResponseValidator.class})
@EnableAspectJAutoProxy
class TokenizerControllerResponseValidatorTest {

    @Autowired
    private DummyController controller;

    @Autowired
    private Validator validator;

    @SpyBean
    private TokenizerControllerResponseValidator validatorSpy;

    @Test
    void controllersPointcut_testAOP() throws Throwable {
        // In this case use the current TokenizerControllerResponseValidator because I want
        // to test AOP behaviour when a method of controller is executed

        // call controller method to trigger AOP
        controller.voidMethodMono();

        // verify that validateResponse method is called
        verify(validatorSpy, Mockito.times(1))
                .validateResponse(any());
        verifyNoMoreInteractions(validatorSpy);

    }
    @Test
    void controllersPointcut_returnNotVoidWithMonoButInvalid() throws Throwable {
        // Create a mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        // In this case I create a new instance of TokenizerControllerResponseValidator because I want to test it
        // outside AOP context
        TokenizerControllerResponseValidator mockValidator = new TokenizerControllerResponseValidator(validator);
        // Create a not valid Mono
        Mono<Object> notValidMono = controller.notVoidMethodInvalidMonoResult();

        // Mock the behavior of the join point to return a notValidMono when called
        Mockito.when(joinPoint.proceed()).thenReturn(notValidMono);

        // Call the advice method
        Mono<Object> result = mockValidator.validateResponse(joinPoint);

        StepVerifier.create(result).expectError(ResponseValidationException.class).verify();
    }

    @Test
    void controllersPointcut_returnNotVoidWithMono() throws Throwable {
        // Create a mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        // In this case I create a new instance of TokjenizerControllerResponseValidator because I want to test it
        // outside AOP context
        TokenizerControllerResponseValidator mockValidator = new TokenizerControllerResponseValidator(validator);
        // Create a valid Mono
        Mono<Object> validMono = controller.notVoidMethodValidMonoResult();

        // Mock the behavior of the join point to return a notValidMono when called
        Mockito.when(joinPoint.proceed()).thenReturn(validMono);

        // Call the advice method
        Mono<Object> result = mockValidator.validateResponse(joinPoint);

        StepVerifier.create(result).expectNextCount(1).verifyComplete();
    }

    @Test
    void controllersPointcut_returnVoidWithMono() throws Throwable {
        // Create a mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);

        TokenizerControllerResponseValidator mockValidator = new TokenizerControllerResponseValidator(validator);
        // Create a void Mono
        Mono<Void> voidMono = controller.voidMethodMono();

        // Mock the behavior of the join point to return a notValidMono when called
        Mockito.when(joinPoint.proceed()).thenReturn(voidMono);

        // Call the advice method
        Mono<Object> result = mockValidator.validateResponse(joinPoint);

        StepVerifier.create(result).expectNextCount(0).verifyComplete();
    }

    @Test
    void controllersPointcut() {
        assertDoesNotThrow(() -> validatorSpy.controllersPointcut());
    }

}