package it.pagopa.pdv.tokenizer.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

import java.util.Collection;

@Slf4j
@Configuration
@PropertySource("classpath:/config/web.properties")
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    private final Collection<WebFilter> filters;

    public WebConfig(Collection<WebFilter> filters) {
        log.trace("Initializing {}", WebConfig.class.getSimpleName());
        this.filters = filters;
    }

}

