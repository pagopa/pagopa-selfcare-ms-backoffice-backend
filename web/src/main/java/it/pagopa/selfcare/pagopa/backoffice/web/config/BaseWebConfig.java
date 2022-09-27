package it.pagopa.selfcare.pagopa.backoffice.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

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


//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.registerModule(new Jdk8Module());
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.setTimeZone(TimeZone.getDefault());
//        return mapper;
//    }

}
