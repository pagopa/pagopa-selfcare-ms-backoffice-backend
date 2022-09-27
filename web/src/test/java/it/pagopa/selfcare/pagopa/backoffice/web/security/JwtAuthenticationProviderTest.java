package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({MockitoExtension.class})
class JwtAuthenticationProviderTest {

    private static final String MDC_UID = "uid";
    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_EMAIL = "email";

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    private JwtService jwtServiceMock;
    @Mock
    private AuthoritiesRetriever authoritiesRetrieverMock;


    @Test
    void authenticate_nullAuth() {
        // given
        Authentication authentication = null;
        // when
        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);
        // then
        Assertions.assertThrows(RuntimeException.class, executable);
        Mockito.verifyNoInteractions(jwtServiceMock, authoritiesRetrieverMock);
    }


    @Test
    void authenticate_invalidToken() {
        // given
        String token = "token";
        Authentication authentication = new JwtAuthenticationToken(token);
        Mockito.doThrow(RuntimeException.class)
                .when(jwtServiceMock)
                .getClaims(Mockito.any());
        // when
        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);
        // then
        Assertions.assertThrows(JwtAuthenticationException.class, executable);
        Assertions.assertNull(MDC.get(MDC_UID));
        Mockito.verify(jwtServiceMock, Mockito.times(1))
                .getClaims(token);
        Mockito.verifyNoMoreInteractions(jwtServiceMock);
        Mockito.verifyNoInteractions(authoritiesRetrieverMock);
    }


    @Test
    void authenticate_koAuthoritiesRetriever() {
        // given
        String token = "token";
        Authentication authentication = new JwtAuthenticationToken(token);
        Mockito.when(jwtServiceMock.getClaims(any()))
                .thenReturn(Mockito.mock(Claims.class));
        Mockito.doThrow(RuntimeException.class)
                .when(authoritiesRetrieverMock)
                .retrieveAuthorities();
        // when
        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);
        // then
        Assertions.assertThrows(AuthoritiesRetrieverException.class, executable);
        Assertions.assertNull(MDC.get(MDC_UID));
        Mockito.verify(jwtServiceMock, Mockito.times(1))
                .getClaims(token);
        Mockito.verify(authoritiesRetrieverMock, Mockito.times(1))
                .retrieveAuthorities();
        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
    }


    @Test
    void authenticate_nullUidAndNullAuthorities() {
        // given
        String token = "token";
        Authentication authentication = new JwtAuthenticationToken(token);
        Mockito.when(jwtServiceMock.getClaims(any()))
                .thenReturn(Mockito.mock(Claims.class));
        // when
        Authentication authenticate = jwtAuthenticationProvider.authenticate(authentication);
        // then
        Assertions.assertNull(MDC.get(MDC_UID));
        Assertions.assertNotNull(authenticate);
        Assertions.assertEquals(token, authenticate.getCredentials());
        Assertions.assertEquals(SelfCareUser.class, authenticate.getPrincipal().getClass());
        Assertions.assertEquals("uid_not_provided", ((SelfCareUser) authenticate.getPrincipal()).getId());
        Assertions.assertNotNull(authenticate.getAuthorities());
        Assertions.assertTrue(authenticate.getAuthorities().isEmpty());
        Mockito.verify(jwtServiceMock, Mockito.times(1))
                .getClaims(token);
        Mockito.verify(authoritiesRetrieverMock, Mockito.times(1))
                .retrieveAuthorities();
        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
    }


    @Test
    void authenticate() {
        // given
        String token = "token";
        Authentication authentication = new JwtAuthenticationToken(token);
        String uid = "uid";
        String email = "email@prova.com";
        Mockito.when(jwtServiceMock.getClaims(any()))
                .thenReturn(new DefaultClaims(Map.of(CLAIM_UID, uid, CLAIM_EMAIL, email)));
        String role = "role";
        Mockito.when(authoritiesRetrieverMock.retrieveAuthorities())
                .thenReturn(List.of(new SimpleGrantedAuthority(role), new SimpleGrantedAuthority(role), new SimpleGrantedAuthority(role)));
        // when
        Authentication authenticate = jwtAuthenticationProvider.authenticate(authentication);
        // then
        Assertions.assertEquals(uid, MDC.get(MDC_UID));
        Assertions.assertNotNull(authenticate);
        Assertions.assertEquals(token, authenticate.getCredentials());
        Assertions.assertEquals(SelfCareUser.class, authenticate.getPrincipal().getClass());
        Assertions.assertEquals(uid, ((SelfCareUser) authenticate.getPrincipal()).getId());
        Assertions.assertEquals(email, ((SelfCareUser) authenticate.getPrincipal()).getEmail());
        Assertions.assertNotNull(authenticate.getAuthorities());
        Assertions.assertEquals(3, authenticate.getAuthorities().size());
        authenticate.getAuthorities().forEach(grantedAuthority -> Assertions.assertEquals(role, grantedAuthority.getAuthority()));
        Mockito.verify(jwtServiceMock, Mockito.times(1))
                .getClaims(token);
        Mockito.verify(authoritiesRetrieverMock, Mockito.times(1))
                .retrieveAuthorities();
        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
    }


    @Test
    void supports_ko() {
        // given
        Class<?> authentication = Object.class;
        // when
        boolean supported = jwtAuthenticationProvider.supports(authentication);
        // then
        Assertions.assertFalse(supported);
    }


    @Test
    void supports_ok() {
        // given
        Class<?> authentication = JwtAuthenticationToken.class;
        // when
        boolean supported = jwtAuthenticationProvider.supports(authentication);
        // then
        Assertions.assertTrue(supported);
    }

}
