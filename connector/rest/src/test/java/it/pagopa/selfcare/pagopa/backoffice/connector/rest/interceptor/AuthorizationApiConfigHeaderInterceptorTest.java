package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthorizationApiConfigHeaderInterceptorTest {
 
    private AuthorizationApiConfigHeaderInterceptor interceptor;
    private RequestTemplate template;
    private SelfCareUser user;

    @BeforeEach
     void setup() {
        interceptor = new AuthorizationApiConfigHeaderInterceptor();
        template = new RequestTemplate();
        user = mock(SelfCareUser.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(mock(Authentication.class));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
     void testApply() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        RequestTemplate template = new RequestTemplate();
        user = mock(SelfCareUser.class);

        ReflectionTestUtils.setField(interceptor, "apiConfigSubscriptionKey", "sub-key");
        ReflectionTestUtils.setField(interceptor, "flagAuthorization", true);

        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn("user-id");

        interceptor.apply(template);
    }

    @Test
     void testCheck_withValidParam() {
        when(user.getOrgVat()).thenReturn("validVat");
        assertDoesNotThrow(() -> interceptor.check("paramName", template, user));
    }

    @Test
     void testCheck_withInvalidParam() {
        when(user.getOrgVat()).thenReturn("paramName1");
        template.query("paramName","paramName");
        assertThrows(RuntimeException.class, () -> interceptor.check("paramName", template, user));
    }
}
