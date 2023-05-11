package it.pagopa.pdv.tokenizer.web.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class LogRequestInterceptorTest {

    private static final String MESSAGE_TEMPLATE = "Requested %s %s" + System.lineSeparator();

    private final LogRequestInterceptor logRequestInterceptorUnderTest = new LogRequestInterceptor();

    private WebFilterChain webFilterChainMock = exchange -> Mono.empty();


    @Test
    void filter_notSkipLog(CapturedOutput output) {
        // given
        String URL_PATH = "test/foo";
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.get(URL_PATH).build();
        MockServerWebExchange mockServerWebExchange = MockServerWebExchange.from(mockServerHttpRequest);
        String expectedSuffix = String.format(MESSAGE_TEMPLATE, mockServerHttpRequest.getMethod(), mockServerHttpRequest.getURI().getPath());
        // when
        logRequestInterceptorUnderTest.filter(mockServerWebExchange, webFilterChainMock);
        // then
        assertTrue(output.getOut().endsWith(expectedSuffix));
    }


    @Test
    void filter_skipLog(CapturedOutput output) {
        // given
        String URL_PATH = "/swagger-resources";
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.get(URL_PATH).build();
        MockServerWebExchange mockServerWebExchange = MockServerWebExchange.from(mockServerHttpRequest);
        String expectedSuffix = String.format(MESSAGE_TEMPLATE, mockServerHttpRequest.getMethod(), mockServerHttpRequest.getURI().getPath());
        // when
        logRequestInterceptorUnderTest.filter(mockServerWebExchange, webFilterChainMock);
        // then
        assertFalse(output.getOut().endsWith(expectedSuffix));
    }

}