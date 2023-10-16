package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import it.pagopa.selfcare.pagopa.backoffice.core.Secret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Concrete Factory of {@link JwtAuthenticationStrategy}
 */
@Slf4j
@Service
public class JwtAuthenticationStrategyFactoryImpl implements JwtAuthenticationStrategyFactory {

    private final BeanFactory beanFactory;


    @Autowired
    public JwtAuthenticationStrategyFactoryImpl(final BeanFactory beanFactory) {
        log.trace("Initializing {}", JwtAuthenticationStrategyFactoryImpl.class.getSimpleName());
        this.beanFactory = beanFactory;
    }


    @Override
    public JwtAuthenticationStrategy create(@Secret final String jwt) {
        final JwtAuthenticationStrategy bean;
        final String issuer;
        try {
            issuer = Jwts.parser()
                    .parseClaimsJwt(Jwts.parser().isSigned(jwt)
                            ? jwt.substring(0, jwt.lastIndexOf('.') + 1)
                            : jwt)
                    .getBody()
                    .getIssuer();
        } catch (JwtException e) {
            throw new JwtAuthenticationException(e.getMessage());
        }
        switch (issuer) {
            case "https://api.platform.pagopa.it":
                bean = beanFactory.getBean("pagopaPRODAuthenticationStrategy", PagopaPRODAuthenticationStrategy.class);
                break;
            default:
                bean = beanFactory.getBean("pagopaAuthenticationStrategy", PagopaAuthenticationStrategy.class);
                break;
        }
        return bean;
    }

}
