package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.APIM_SUBSCRIPTION_KEY;

public class TaxonomyFeignConfig extends BaseFeignConfig {

    @Value("${authorization.taxonomy.subscriptionKey}")
    private String taxonomySubscriptionKey;


    @Bean
    public RequestInterceptor subscriptionKey() {
        return requestTemplate -> requestTemplate.header(APIM_SUBSCRIPTION_KEY, taxonomySubscriptionKey);
    }

}
