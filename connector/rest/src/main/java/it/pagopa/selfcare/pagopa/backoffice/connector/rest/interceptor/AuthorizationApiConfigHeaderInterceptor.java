package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthorizationApiConfigHeaderInterceptor implements RequestInterceptor {
    @Value("${authorization.api-config.subscriptionKey}")
    private String apiConfigSubscriptionKey;

    private static final List<String> PARAMS_NAME = List.of("","");
    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SelfCareUser user = (SelfCareUser) auth.getPrincipal();
        check(PARAMS_NAME,template,user);
        template.header("x-selfcare-uid", user.getId());
        template.removeHeader("Ocp-Apim-Subscription-Key")
                .header("Ocp-Apim-Subscription-Key", apiConfigSubscriptionKey);
    }

    private void check(String paramName,RequestTemplate template,SelfCareUser user) {
        if((template.queries().containsKey(paramName)&& !(template.queries().get(paramName).toArray()[0]==user.getOrgVat())))
            throw new RuntimeException(new NoPermissionException(""));

    }

    private void check(List<String> paramsName,RequestTemplate template,SelfCareUser user) {
       paramsName.forEach(param->check(param,template,user));

    }
}