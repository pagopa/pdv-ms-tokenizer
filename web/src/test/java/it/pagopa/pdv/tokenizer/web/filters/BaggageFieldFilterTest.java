package it.pagopa.pdv.tokenizer.web.filters;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class BaggageFieldFilterTest {

    @Test
    void doFilterInternal_withBaggage() {
        //given
        Tracer tracer = Mockito.mock(Tracer.class);
        Baggage baggage = Mockito.mock(Baggage.class);
        Mockito.when(tracer.getBaggage("x-amzn-trace-id")).thenReturn(baggage);
        Mockito.when(baggage.get()).thenReturn("test");
        BaggageFieldFilter baggageFieldFilter = new BaggageFieldFilter(tracer);

        MockFilterChain mockChain = new MockFilterChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse rsp = new MockHttpServletResponse();

        //then

        assertDoesNotThrow(() -> baggageFieldFilter.doFilter(req, rsp, mockChain));

    }

    @Test
    void doFilterInternal_withoutBaggage(){
        //given
        Tracer tracer = Mockito.mock(Tracer.class);
        Baggage baggage = Mockito.mock(Baggage.class);
        Mockito.when(tracer.getBaggage("x-amzn-trace-id")).thenReturn(baggage);
        Mockito.when(baggage.get()).thenReturn(null);
        BaggageFieldFilter baggageFieldFilter = new BaggageFieldFilter(tracer);

        MockFilterChain mockChain = new MockFilterChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse rsp = new MockHttpServletResponse();

        //then

        assertDoesNotThrow(() -> baggageFieldFilter.doFilter(req, rsp, mockChain));

    }
}

