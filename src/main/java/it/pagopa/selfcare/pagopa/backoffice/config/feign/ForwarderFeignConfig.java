package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;

public class ForwarderFeignConfig extends BaseFeignConfig {
    @Value("${authorization.forwarder.subscriptionKey}")
    private String forwarderSubscriptionKey;

    @Bean
    public RequestInterceptor subscriptionKey() {
        return requestTemplate -> requestTemplate.header(APIM_SUBSCRIPTION_KEY, forwarderSubscriptionKey);
    }
}
