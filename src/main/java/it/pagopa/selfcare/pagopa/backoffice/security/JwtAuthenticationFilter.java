package it.pagopa.selfcare.pagopa.backoffice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static it.pagopa.selfcare.pagopa.backoffice.handler.RestExceptionsHandler.UNHANDLED_EXCEPTION;
import static it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil.getJwtFromRequest;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private PagopaAuthenticationStrategy pagopaAuthenticationStrategy;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain filterChain) throws ServletException, IOException {

        try {
            final JwtAuthenticationToken authRequest = new JwtAuthenticationToken(getJwtFromRequest(request));
            final JwtAuthenticationToken jwtAuthenticationToken = pagopaAuthenticationStrategy.authenticate(authRequest);

            jwtAuthenticationToken.setDetails(authRequest.getDetails());
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.warn("Cannot set user authentication", e);
            filterChain.doFilter(request, response);
        } catch (final Exception e) {
            log.error(UNHANDLED_EXCEPTION, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            final Problem problem = new Problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            response.getOutputStream().print(objectMapper.writeValueAsString(problem));
        } finally {
            SecurityContextHolder.clearContext();
            MDC.clear();
        }
    }


}
