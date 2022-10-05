package it.pagopa.selfcare.pagopa.backoffice.web.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(AuthoritiesRetriever.class)
public class AuthoritiesRetrieverAutoconfiguration {

    @Bean
    public AuthoritiesRetriever authoritiesRetriever() {
        return new NoAuthoritiesRetriever();
    }

}
