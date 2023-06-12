package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.PermissionDeniedException;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;

@Slf4j
@Service
public class AuthorizationApiConfigHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.api-config.subscriptionKey}")
    private String apiConfigSubscriptionKey;

    @Value("${authorization.api-config.flag-authorization}")
    private String flagAuthorization;

    private static final List<String> EMAIL_AUTHORIZED = List.of(
            "stefano.bafaro@pagopa.it", "giovanna94@libero.it", "aaron77@poste.it");

    private static final List<String> PARAMS_NAME = List.of(
            "stationId", "ecCode", "stationcode", "code", "brokerId", "stationCode", "creditorInstitutionCode",
            "brokerCode", "pspcode", "channelcode", "brokerpspcode", "channelId");

    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SelfCareUser user = (SelfCareUser) auth.getPrincipal();
        if (!Boolean.parseBoolean(flagAuthorization) && !EMAIL_AUTHORIZED.contains(user.getEmail().toLowerCase())) {
            check(template, user);
        }
        template.header("x-selfcare-uid", user.getId());
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .header("Ocp-Apim-Subscription-Key", apiConfigSubscriptionKey);
    }

    void check(String paramName, RequestTemplate template, SelfCareUser user) {
        if ((template.queries().containsKey(paramName) && !(template.queries().get(paramName).contains(user.getOrgVat())))){
            log.debug("Request bloked= {} in method= {}", template.url(), template.method());
            throw new PermissionDeniedException("This action is not permitted by current user");
        }
    }

    void check(RequestTemplate template, SelfCareUser user) {
        AuthorizationApiConfigHeaderInterceptor.PARAMS_NAME.forEach(param -> check(param, template, user));
    }
}