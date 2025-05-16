package it.pagopa.selfcare.pagopa.backoffice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ContentSecurityPolicyFilter implements Filter {

    private String imgSource;

    public ContentSecurityPolicyFilter(
            @Value("${authorization.institutions.contentSecurityPolicy}") String imgSource
    ) {
        this.imgSource = imgSource;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) servletResponse;
        httpResp.setHeader("Content-Security-Policy", "default-src 'self'; img-src 'self' " + imgSource + ";");

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
