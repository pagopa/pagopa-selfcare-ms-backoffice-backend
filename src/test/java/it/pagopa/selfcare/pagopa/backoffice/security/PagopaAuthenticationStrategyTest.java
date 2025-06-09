package it.pagopa.selfcare.pagopa.backoffice.security;

import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagopaAuthenticationStrategyTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PagopaAuthenticationStrategy authenticationStrategy;

    @Mock
    private JwtAuthenticationToken authentication;

    @Mock
    private Claims claims;

    @Test
    void authenticateReturnsTokenWithValidClaims() {
        when(jwtUtil.getClaims(authentication)).thenReturn(claims);
        when(claims.get("uid", String.class)).thenReturn("valid_uid");
        when(claims.get("email", String.class)).thenReturn("user@example.com");
        when(claims.get("name", String.class)).thenReturn("John");
        when(claims.get("family_name", String.class)).thenReturn("Doe");
        when(claims.get("org_vat", String.class)).thenReturn("123456789");
        when(claims.get("org_id", String.class)).thenReturn("org123");
        when(claims.get("org_party_role", String.class)).thenReturn("manager");
        when(claims.get("org_role", String.class)).thenReturn("admin");

        JwtAuthenticationToken result = authenticationStrategy.authenticate(authentication);

        assertNotNull(result);
        assertEquals("valid_uid", ((SelfCareUser) result.getPrincipal()).getId());
        assertEquals("valid_uid", ((SelfCareUser) result.getPrincipal()).getName());
        assertEquals("user@example.com", ((SelfCareUser) result.getPrincipal()).getEmail());
        assertEquals("John", ((SelfCareUser) result.getPrincipal()).getUserName());
        assertEquals("Doe", ((SelfCareUser) result.getPrincipal()).getSurname());
        assertEquals("123456789", ((SelfCareUser) result.getPrincipal()).getOrgVat());
        assertEquals("org123", ((SelfCareUser) result.getPrincipal()).getOrgId());
        assertEquals("manager", ((SelfCareUser) result.getPrincipal()).getOrgPartyRole());
        assertEquals("admin", ((SelfCareUser) result.getPrincipal()).getOrgRole());
    }

    @Test
    void authenticateThrowsAppExceptionWhenClaimsAreInvalid() {
        when(jwtUtil.getClaims(authentication)).thenThrow(new RuntimeException("Invalid token"));

        AppException exception = assertThrows(AppException.class, () -> authenticationStrategy.authenticate(authentication));

        assertEquals(AppError.UNAUTHORIZED.title, exception.getTitle());
    }

    @Test
    void authenticateHandlesMissingUidGracefully() {
        when(jwtUtil.getClaims(authentication)).thenReturn(claims);
        when(claims.get("uid", String.class)).thenReturn(null);

        JwtAuthenticationToken result = authenticationStrategy.authenticate(authentication);

        assertNotNull(result);
        assertEquals("uid_not_provided", ((SelfCareUser) result.getPrincipal()).getId());
    }
}