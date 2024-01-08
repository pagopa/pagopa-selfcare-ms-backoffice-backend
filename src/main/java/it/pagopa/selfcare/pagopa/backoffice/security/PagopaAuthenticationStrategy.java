package it.pagopa.selfcare.pagopa.backoffice.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.pagopa.backoffice.exception.AuthoritiesRetrieverException;
import it.pagopa.selfcare.pagopa.backoffice.exception.JwtAuthenticationException;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class PagopaAuthenticationStrategy {


    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_SURNAME = "family_name";
    private static final String CLAIM_ORG_VAT = "org_vat";
    private final JwtUtil jwtUtil;


    @Autowired
    public PagopaAuthenticationStrategy(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    public JwtAuthenticationToken authenticate(JwtAuthenticationToken authentication) throws AuthenticationException {
        SelfCareUser user;
        try {
            Claims claims = jwtUtil.getClaims(authentication);

            Optional<String> uid = Optional.ofNullable(claims.get(CLAIMS_UID, String.class));
            uid.ifPresentOrElse(value -> MDC.put(MDC_UID, value),
                    () -> log.warn("uid claims is null"));

            user = SelfCareUser.builder(uid.orElse("uid_not_provided"))
                    .email(claims.get(CLAIM_EMAIL, String.class))
                    .name(claims.get(CLAIM_NAME, String.class))
                    .surname(claims.get(CLAIM_SURNAME, String.class))
                    .orgVat(claims.get(CLAIM_ORG_VAT, String.class))
                    .build();

        } catch (Exception e) {
            MDC.remove(MDC_UID);
            throw new JwtAuthenticationException(e.getMessage(), e);
        }

        final Collection<GrantedAuthority> authorities;
        try {
            authorities = null;
        } catch (Exception e) {
            MDC.remove(MDC_UID);
            throw new AuthoritiesRetrieverException("An error occurred during authorities retrieval", e);
        }
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication.getCredentials(), user, authorities);
        authenticationToken.setDetails(authentication.getDetails());

        return authenticationToken;
    }
}
