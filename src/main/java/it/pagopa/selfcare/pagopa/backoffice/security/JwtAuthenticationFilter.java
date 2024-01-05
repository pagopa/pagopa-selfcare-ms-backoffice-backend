package it.pagopa.selfcare.pagopa.backoffice.security;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil.getJwtFromRequest;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private PagopaAuthenticationStrategy pagopaAuthenticationStrategy;

    @Value("${info.properties.environment}")
    private String env;


    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain filterChain) throws ServletException, IOException {

        try {
            // skip jwt-check during junit test
            // the user in the security context is mocked
            if("test".equals(env)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            final JwtAuthenticationToken authRequest = new JwtAuthenticationToken(getJwtFromRequest(request));
            final JwtAuthenticationToken jwtAuthenticationToken = pagopaAuthenticationStrategy.authenticate(authRequest);

            jwtAuthenticationToken.setDetails(authRequest.getDetails());
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.warn("Cannot set user authentication", e);
            filterChain.doFilter(request, response);
        } catch (final Exception e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        } finally {
            SecurityContextHolder.clearContext();
            MDC.clear();
        }
    }


}
