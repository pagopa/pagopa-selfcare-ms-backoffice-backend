package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestLine;
import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.core.Secret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Slf4j
@Service
public class AuthorizationHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.external-api.subscriptionKey}")
    private String externalApiSubscriptionKey;

    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Method method = template.methodMetadata().method();

        if (!method.isAnnotationPresent(RequestLine.class)) {
            SelfCareUser user = (SelfCareUser) auth.getPrincipal();
            template.header("x-selfcare-uid", user.getId());
        }
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .header("Ocp-Apim-Subscription-Key", externalApiSubscriptionKey);
    }
}
