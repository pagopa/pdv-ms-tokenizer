package it.pagopa.pdv.tokenizer.web.filters;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class BaggageFieldFilter extends OncePerRequestFilter {
    private Tracer tracer;

    private static final String field = "x-amzn-trace-id";

    private static final String BAGGAGE_SETTING_EXCEPTION = "Exception during baggage field setting";

    BaggageFieldFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            String rawBaggage = tracer.getBaggage(field).get();
            if (rawBaggage != null) {
                tracer.createBaggage(field, rawBaggage.replaceAll("=", ":"));
            }
        } catch (Exception e) {
            log.error(BAGGAGE_SETTING_EXCEPTION, e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
