package it.pagopa.selfcare.pagopa.backoffice.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Problem;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler.UNHANDLED_EXCEPTION;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;


    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        log.trace("doFilterInternal");
        try {

            try {
                final JwtAuthenticationToken authRequest = new JwtAuthenticationToken(parseJwt(request));
                final Authentication authentication = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (AuthenticationException e) {
                log.error("Cannot set user authentication", e);
                filterChain.doFilter(request, response);
            } catch (final Exception e) {
                log.error(UNHANDLED_EXCEPTION, e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                final Problem problem = new Problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                response.getOutputStream().print(objectMapper.writeValueAsString(problem));
            }

        } finally {
            SecurityContextHolder.clearContext();
            MDC.clear();
        }
    }


    private String parseJwt(HttpServletRequest request) {
        log.trace("parseJwt");
        String jwt = null;
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        return jwt;
    }

}
