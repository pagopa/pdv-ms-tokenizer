package it.pagopa.pdv.tokenizer.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
//@EnableWebSecurity
class SecurityConfig {//TODO change Name

    public SecurityConfig() {
        log.trace("Initializing {}", SecurityConfig.class.getSimpleName());
    }

}