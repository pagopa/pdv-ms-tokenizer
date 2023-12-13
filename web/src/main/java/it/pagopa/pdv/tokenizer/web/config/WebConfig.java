package it.pagopa.pdv.tokenizer.web.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ECSPlugin;
import com.amazonaws.xray.strategy.jakarta.SegmentNamingStrategy;
import com.amazonaws.xray.strategy.sampling.CentralizedSamplingStrategy;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Slf4j
@Configuration
@PropertySource("classpath:/config/web.properties")
class WebConfig implements WebMvcConfigurer {

    private final Collection<HandlerInterceptor> interceptors;

    private final ApplicationContext applicationContext;



    public WebConfig(Collection<HandlerInterceptor> interceptors, ApplicationContext applicationContext) {
        log.trace("Initializing {}", WebConfig.class.getSimpleName());
        this.interceptors = interceptors;
        this.applicationContext = applicationContext;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptors != null) {
            interceptors.forEach(registry::addInterceptor);
        }
    }
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new ECSPlugin()).withPlugin(new EC2Plugin());
        builder.withSamplingStrategy(new CentralizedSamplingStrategy());
        AWSXRay.setGlobalRecorder(builder.build());
    }
    @Bean
    public Filter TracingFilter() {
        return new AWSXRayServletFilter(SegmentNamingStrategy.dynamic(this.applicationContext.getId()));
    }
}
