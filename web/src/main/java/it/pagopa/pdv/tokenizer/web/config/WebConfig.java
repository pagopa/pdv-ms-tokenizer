package it.pagopa.pdv.tokenizer.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Slf4j
@Configuration
@PropertySource("classpath:/config/web.properties")
class WebConfig implements WebMvcConfigurer {

    private final Collection<HandlerInterceptor> interceptors;


    public WebConfig(Collection<HandlerInterceptor> interceptors) {
        log.trace("Initializing {}", WebConfig.class.getSimpleName());
        this.interceptors = interceptors;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptors != null) {
            interceptors.forEach(registry::addInterceptor);
        }
    }

}
