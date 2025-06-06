package it.pagopa.selfcare.pagopa.backoffice.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Optional;

@Slf4j
public class PagopaAuthenticationStrategy {


    private static final String MDC_UID = "uid";
    private static final String CLAIMS_UID = "uid";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_SURNAME = "family_name";
    private static final String CLAIM_ORG_VAT = "org_vat";
    private static final String CLAIM_ORG_ID = "org_id";
    private static final String CLAIM_ORG_PARTY_ROLE = "org_party_role";
    private static final String CLAIM_ORG_ROLE = "org_role";
    private final JwtUtil jwtUtil;

    public PagopaAuthenticationStrategy(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtAuthenticationToken authenticate(JwtAuthenticationToken authentication) {
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
                    .orgId(claims.get(CLAIM_ORG_ID, String.class))
                    .orgPartyRole(claims.get(CLAIM_ORG_PARTY_ROLE, String.class))
                    .orgRole(claims.get(CLAIM_ORG_ROLE, String.class))
                    .build();

        } catch (Exception e) {
            MDC.remove(MDC_UID);
            throw new AppException(AppError.UNAUTHORIZED, e);
        }

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication.getCredentials(), user, null);
        authenticationToken.setDetails(authentication.getDetails());

        return authenticationToken;
    }
}
