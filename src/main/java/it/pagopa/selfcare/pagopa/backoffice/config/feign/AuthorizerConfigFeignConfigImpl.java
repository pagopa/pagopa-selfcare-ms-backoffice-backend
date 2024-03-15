package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;

/**
 * Configuration class for Authorizer Config cline
 */
public class AuthorizerConfigFeignConfigImpl extends BaseFeignConfig {

    @Value("${authorization.authorizer-config.subscriptionKey}")
    private String authorizerConfigSubscriptionKey;


    @Bean
    public RequestInterceptor subscriptionKey() {
        return requestTemplate -> requestTemplate.header(APIM_SUBSCRIPTION_KEY, authorizerConfigSubscriptionKey);
    }
}
