package it.pagopa.pdv.tokenizer.web.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class LogRequestInterceptorTest {

    private static final String MESSAGE_TEMPLATE = "Requested %s %s" + System.lineSeparator();

    private final LogRequestInterceptor logRequestInterceptorUnderTest = new LogRequestInterceptor();


    @Test
    void preHandle_skipLog(CapturedOutput output) {
        // given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String expectedSuffix = String.format(MESSAGE_TEMPLATE, mockHttpServletRequest.getMethod(), mockHttpServletRequest.getRequestURI());
        // when
        boolean result = logRequestInterceptorUnderTest.preHandle(mockHttpServletRequest, mockHttpServletResponse, "controller");
        // then
        assertTrue(result);
        assertTrue(output.getOut().endsWith(expectedSuffix));
    }


    @Test
    void preHandle_notSkipLog(CapturedOutput output) {
        // given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI("/swagger-resources");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String expectedSuffix = String.format(MESSAGE_TEMPLATE, mockHttpServletRequest.getMethod(), mockHttpServletRequest.getRequestURI());

        // when
        boolean result = logRequestInterceptorUnderTest.preHandle(mockHttpServletRequest, mockHttpServletResponse, "controller");
        // then
        assertTrue(result);
        assertFalse(output.getOut().endsWith(expectedSuffix));
    }

}