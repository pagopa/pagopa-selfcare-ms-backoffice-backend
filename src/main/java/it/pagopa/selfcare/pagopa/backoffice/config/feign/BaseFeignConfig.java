package it.pagopa.selfcare.pagopa.backoffice.config.feign;

import feign.RequestInterceptor;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseFeignConfig {

    public static final String SELFCARE_UID = "X-Selfcare-UID";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    /**
     * @return the uid from the security context
     */
    private static String getSelfcareUserUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uid;
        if(auth != null && auth.getPrincipal() != null) {
            SelfCareUser user = (SelfCareUser) auth.getPrincipal();
            uid = user.getId();
        } else {
            uid = "";
        }
        return uid;
    }

    @Bean
    public RequestInterceptor commonHeaderInterceptor() {
        String uid = getSelfcareUserUid();
        return requestTemplate -> requestTemplate
                .header(HEADER_REQUEST_ID, MDC.get("requestId"))
                .header(SELFCARE_UID, uid);
    }

}
