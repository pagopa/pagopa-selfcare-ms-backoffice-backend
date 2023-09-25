package it.pagopa.selfcare.pagopa.backoffice.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PagopaPRODAuthenticationStrategy extends PagopaAuthenticationStrategy {

    public PagopaPRODAuthenticationStrategy(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
        super(jwtService, authoritiesRetriever);
        env = "PROD";
    }

}
