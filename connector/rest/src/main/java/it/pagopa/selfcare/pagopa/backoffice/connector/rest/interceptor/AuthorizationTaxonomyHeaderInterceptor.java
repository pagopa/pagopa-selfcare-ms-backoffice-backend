package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationTaxonomyHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.taxonomy.subscriptionKey}")
    private String taxonomySubscriptionKey;

    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() != null) {
            SelfCareUser user = (SelfCareUser) auth.getPrincipal();
            template.header("x-selfcare-uid", user.getId());
        }
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .header("Ocp-Apim-Subscription-Key", taxonomySubscriptionKey);
    }
}
