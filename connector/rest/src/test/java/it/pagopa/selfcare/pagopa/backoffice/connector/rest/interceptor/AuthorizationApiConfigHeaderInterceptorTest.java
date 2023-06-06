package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationApiConfigHeaderInterceptorTest {
 
    private AuthorizationApiConfigHeaderInterceptor interceptor;
    private RequestTemplate template;
    private SelfCareUser user;

    @BeforeEach
    public void setup() {
        interceptor = new AuthorizationApiConfigHeaderInterceptor();
        template = new RequestTemplate();
        user = mock(SelfCareUser.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(mock(Authentication.class));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCheck_withValidParam() {
        when(user.getOrgVat()).thenReturn("validVat");
        assertDoesNotThrow(() -> interceptor.check("paramName", template, user));
    }

    @Test
    public void testCheck_withInvalidParam() {
        when(user.getOrgVat()).thenReturn("paramName1");
        template.query("paramName","paramName");
        assertThrows(RuntimeException.class, () -> interceptor.check("paramName", template, user));
    }
}
