package it.pagopa.selfcare.pagopa.backoffice.security;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil.getJwtFromRequest;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain filterChain) throws ServletException, IOException {

        try {
            final JwtAuthenticationToken authRequest = new JwtAuthenticationToken(getJwtFromRequest(request));
            final JwtAuthenticationToken jwtAuthenticationToken = new PagopaAuthenticationStrategy(jwtUtil).authenticate(authRequest);

            jwtAuthenticationToken.setDetails(authRequest.getDetails());
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            filterChain.doFilter(request, response);
        } catch (AppException e) {
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
