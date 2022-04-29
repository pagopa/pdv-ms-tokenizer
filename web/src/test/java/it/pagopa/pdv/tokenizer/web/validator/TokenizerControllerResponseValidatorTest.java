package it.pagopa.pdv.tokenizer.web.validator;

import it.pagopa.pdv.tokenizer.web.controller.DummyController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(classes = {
        ValidationAutoConfiguration.class,
        DummyController.class,
        TokenizerControllerResponseValidator.class})
@EnableAspectJAutoProxy
class TokenizerControllerResponseValidatorTest {

    @Autowired
    private DummyController controller;

    @SpyBean
    private TokenizerControllerResponseValidator validatorSpy;


    @Test
    void controllersPointcut_returnNotVoid() {
        controller.notVoidMethod();
        Mockito.verify(validatorSpy, Mockito.times(1))
                .validateResponse(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(validatorSpy);
    }


    @Test
    void controllersPointcut_returnVoid() {
        controller.voidMethod();
        Mockito.verify(validatorSpy, Mockito.times(1))
                .validateResponse(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(validatorSpy);
    }


    @Test
    void controllersPointcut() {
        validatorSpy.controllersPointcut();
    }

}