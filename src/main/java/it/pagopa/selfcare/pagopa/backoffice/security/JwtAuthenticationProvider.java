package it.pagopa.selfcare.pagopa.backoffice.security;

import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {


    private final PagopaAuthenticationStrategy jwtAuthenticationStrategy;

    public JwtAuthenticationProvider(PagopaAuthenticationStrategy jwtAuthenticationStrategy) {
        log.trace("Initializing {}", JwtAuthenticationProvider.class.getSimpleName());
        this.jwtAuthenticationStrategy = jwtAuthenticationStrategy;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.trace("authenticate start");
        log.debug(Constants.CONFIDENTIAL_MARKER, "authenticate authentication = {}", authentication);
        final JwtAuthenticationToken requestAuth = (JwtAuthenticationToken) authentication;
        final JwtAuthenticationToken jwtAuthenticationToken = jwtAuthenticationStrategy.authenticate(requestAuth);
        jwtAuthenticationToken.setDetails(authentication.getDetails());
        log.debug(Constants.CONFIDENTIAL_MARKER, "authenticate result = {}", jwtAuthenticationToken);
        log.trace("authenticate end");
        return jwtAuthenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
