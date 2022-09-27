package it.pagopa.selfcare.pagopa.backoffice.web.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@FunctionalInterface
public interface AuthoritiesRetriever {

    Collection<GrantedAuthority> retrieveAuthorities();

}
