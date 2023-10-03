package it.pagopa.selfcare.pagopa.backoffice.connector.rest.interceptor;

import feign.RequestTemplate;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationTaxonomyHeaderInterceptorTest {

    private AuthorizationTaxonomyHeaderInterceptor interceptor;
    private RequestTemplate template;
    private SelfCareUser user;

    @BeforeEach
    void setup() {
        interceptor = new AuthorizationTaxonomyHeaderInterceptor();
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

        ReflectionTestUtils.setField(interceptor, "taxonomySubscriptionKey", "sub-key");

        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn("user-id");

        interceptor.apply(template);
    }

    @Test
    void testApply_auth_null() {
        RequestTemplate template = new RequestTemplate();

        user = mock(SelfCareUser.class);

        ReflectionTestUtils.setField(interceptor, "taxonomySubscriptionKey", "sub-key");
        when(user.getId()).thenReturn("user-id");

        interceptor.apply(template);
    }


}
