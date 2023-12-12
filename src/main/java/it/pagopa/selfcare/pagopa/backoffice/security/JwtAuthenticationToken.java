package it.pagopa.selfcare.pagopa.backoffice.security;

import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.util.Secret;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private Object principal;

    public JwtAuthenticationToken(@Secret final String token) {
        super(null);
        setAuthenticated(false);
        this.token = token;
    }

    public JwtAuthenticationToken(@Secret final String token, final SelfCareUser user, Collection<GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        principal = user;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }
}
