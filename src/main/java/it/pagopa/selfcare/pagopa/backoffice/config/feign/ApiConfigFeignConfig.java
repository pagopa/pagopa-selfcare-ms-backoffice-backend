package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;

public class ApiConfigFeignConfig extends BaseFeignConfig {

    @Value("${authorization.api-config.subscriptionKey}")
    private String apiConfigSubscriptionKey;


    @Bean
    public RequestInterceptor subscriptionKey() {
        return requestTemplate -> requestTemplate.header(APIM_SUBSCRIPTION_KEY, apiConfigSubscriptionKey);
    }

}
