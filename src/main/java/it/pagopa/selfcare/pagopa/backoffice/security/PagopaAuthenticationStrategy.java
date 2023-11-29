package it.pagopa.selfcare.pagopa.backoffice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import it.pagopa.selfcare.pagopa.backoffice.exception.AuthoritiesRetrieverException;
import it.pagopa.selfcare.pagopa.backoffice.exception.JwtAuthenticationException;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.JWT_PROD_ISSUER;

@Slf4j
@Service
public class PagopaAuthenticationStrategy {


    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_SURNAME = "family_name";
    private static final String CLAIM_ORG_VAT = "org_vat";
    private final JwtService jwtService;
    private final AuthoritiesRetriever authoritiesRetriever;


    @Autowired
    public PagopaAuthenticationStrategy(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
        this.jwtService = jwtService;
        this.authoritiesRetriever = authoritiesRetriever;
    }

    public JwtAuthenticationToken authenticate(JwtAuthenticationToken authentication) throws AuthenticationException {
        log.trace("authenticate start");
        log.debug(Constants.CONFIDENTIAL_MARKER, "authenticate authentication = {}", authentication);

        SelfCareUser user;
        try {
            boolean isProd = isProdIssuerFromJWT(authentication);
            Claims claims = jwtService.getClaims(authentication.getCredentials(), isProd);

            log.debug(Constants.CONFIDENTIAL_MARKER, "authenticate claims = {}", claims);
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
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication.getCredentials(),
                user,
                authorities);
        authenticationToken.setDetails(authentication.getDetails());

        log.debug(Constants.CONFIDENTIAL_MARKER, "authenticate result = {}", authentication);
        log.trace("authenticate end");
        return authenticationToken;
    }

    /**
     * @param authentication the JWT from the request
     * @return true if the issuer is equals to 'https://api.platform.pagopa.it'
     */
    @SuppressWarnings("java:S5659")
    private static boolean isProdIssuerFromJWT(JwtAuthenticationToken authentication) {
        String jwt = authentication.getCredentials();
        String issuer = ((DefaultClaims) (Jwts.parser().parse(jwt.substring(0, jwt.lastIndexOf('.') + 1)).getBody())).getIssuer();
        return JWT_PROD_ISSUER.equals(issuer);
    }
}
