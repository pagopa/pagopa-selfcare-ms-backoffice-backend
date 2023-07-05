package it.pagopa.selfcare.pagopa.backoffice.web.security;

/**
 * Abstract Factory of {@link JwtAuthenticationStrategy}
 */
public interface JwtAuthenticationStrategyFactory {

    JwtAuthenticationStrategy create(String jwt);

}
