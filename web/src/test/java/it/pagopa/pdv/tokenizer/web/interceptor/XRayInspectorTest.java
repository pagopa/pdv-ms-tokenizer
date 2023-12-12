package it.pagopa.pdv.tokenizer.web.interceptor;

import it.pagopa.pdv.tokenizer.web.controller.DummyController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
        DummyController.class,
        XRayInspector.class
})
@EnableAspectJAutoProxy
public class XRayInspectorTest {
    @Autowired
    private DummyController controller;

    @SpyBean
    private XRayInspector xRayInspectorSpy;

    @Test
    void xRayEnabledClasses_invocation(){
        assertDoesNotThrow(() -> controller.voidMethod());
        verify(xRayInspectorSpy, Mockito.times(1))
                .generateMetadata(any(), any());
    }

    @Test
    void xRayEnabledClasses_pointcut(){assertDoesNotThrow(() -> controller.voidMethod());}
}