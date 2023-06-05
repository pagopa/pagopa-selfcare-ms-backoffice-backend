package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_SURNAME = "family_name";

    private static final String CLAIM_ORG_VAT = "org_vat";
    private final JwtService jwtService;
    private final AuthoritiesRetriever authoritiesRetriever;


    public JwtAuthenticationProvider(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
        this.jwtService = jwtService;
        this.authoritiesRetriever = authoritiesRetriever;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.trace("authenticate start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "authenticate authentication = {}", authentication);
        final JwtAuthenticationToken requestAuth = (JwtAuthenticationToken) authentication;

        SelfCareUser user;
        try {
            Claims claims = jwtService.getClaims(requestAuth.getCredentials());
            log.debug(LogUtils.CONFIDENTIAL_MARKER, "authenticate claims = {}", claims);
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
            authorities = authoritiesRetriever.retrieveAuthorities();
        } catch (Exception e) {
            MDC.remove(MDC_UID);
            throw new AuthoritiesRetrieverException("An error occurred during authorities retrieval", e);
        }
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(requestAuth.getCredentials(),
                user,
                authorities);
        authenticationToken.setDetails(authentication.getDetails());

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "authenticate result = {}", authentication);
        log.trace("authenticate end");
        return authenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
