package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class PagopaPRODAuthenticationStrategy extends PagopaAuthenticationStrategy {

    public PagopaPRODAuthenticationStrategy(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
        super(jwtService, authoritiesRetriever);
        env = "PROD";
    }

}
