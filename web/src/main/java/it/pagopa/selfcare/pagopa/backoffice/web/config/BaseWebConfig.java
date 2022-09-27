package it.pagopa.selfcare.pagopa.backoffice.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Configuration
@ComponentScan(basePackages = {"it.pagopa.selfcare.pagopa.backoffice.web.interceptor", "it.pagopa.selfcare.pagopa.backoffice.web.handler"})
public class BaseWebConfig implements WebMvcConfigurer {

    private final Collection<HandlerInterceptor> interceptors;


    public BaseWebConfig(Collection<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptors != null) {
            interceptors.forEach(registry::addInterceptor);
        }
    }

}
