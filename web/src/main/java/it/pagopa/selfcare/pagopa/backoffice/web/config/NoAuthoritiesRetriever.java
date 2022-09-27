package it.pagopa.selfcare.pagopa.backoffice.web.config;

import it.pagopa.selfcare.pagopa.backoffice.web.security.AuthoritiesRetriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class NoAuthoritiesRetriever implements AuthoritiesRetriever {

    @Override
    public Collection<GrantedAuthority> retrieveAuthorities() {
        log.trace("retrieveAuthorities start");
        log.debug("retrieved authorities = null");
        log.trace("retrieveAuthorities end");
        return null;
    }

}
