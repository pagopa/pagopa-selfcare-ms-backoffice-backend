package it.pagopa.selfcare.pagopa.backoffice.web.security;


import it.pagopa.selfcare.pagopa.backoffice.core.Secret;

/**
 * Abstract Factory of {@link JwtAuthenticationStrategy}
 */
public interface JwtAuthenticationStrategyFactory {

    JwtAuthenticationStrategy create(@Secret String jwt);

}
