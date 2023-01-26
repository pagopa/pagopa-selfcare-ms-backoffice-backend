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
public class AuthorizationApiConfigHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.api-config.subscriptionKey}")
    private String apiConfigSubscriptionKey;

    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SelfCareUser user = (SelfCareUser) auth.getPrincipal();
        template.header("x-selfcare-uid", user.getId());
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .header("Ocp-Apim-Subscription-Key", apiConfigSubscriptionKey);
    }
}