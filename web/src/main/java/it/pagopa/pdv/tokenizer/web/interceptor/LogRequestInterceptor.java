package it.pagopa.pdv.tokenizer.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class LogRequestInterceptor implements WebFilter {

    private static final Collection<String> URI_PREFIX_WHITELIST = List.of(
            "/swagger",
            "/v3/api-docs"
    );


    public LogRequestInterceptor() {
        log.trace("Initializing {}", LogRequestInterceptor.class.getSimpleName());
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        boolean skipLog = URI_PREFIX_WHITELIST.stream()
                .anyMatch(exchange.getRequest().getURI().getPath()::startsWith);
        if (!skipLog) {
            log.info("Requested {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI().getPath());
        }
        return chain.filter(exchange);
    }

}
