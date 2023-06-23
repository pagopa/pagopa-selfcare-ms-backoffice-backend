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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private JwtAuthenticationStrategyFactory jwtAuthenticationStrategyFactoryMock;


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


//    @Test
//    void authenticate_invalidToken() {
//        // given
//        String token = "token";
//        Authentication authentication = new JwtAuthenticationToken(token);
//
//        when(authentication.getCredentials()).thenReturn("dummyCredentials");
//        when(jwtAuthenticationStrategyFactoryMock.create("dummyCredentials")).thenReturn();
//
//        // when
//
//        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);
//        // then
//        //Assertions.assertThrows(JwtAuthenticationException.class, executable);
//        Assertions.assertNull(MDC.get(MDC_UID));
//        verify(jwtServiceMock, Mockito.times(1))
//                .getClaims(token);
//        Mockito.verifyNoMoreInteractions(jwtServiceMock);
//        Mockito.verifyNoInteractions(authoritiesRetrieverMock);
//    }


//    @Test
//    void authenticate_koAuthoritiesRetriever() {
//        // given
//        String token = "token";
//        Authentication authentication = new JwtAuthenticationToken(token);
//        when(jwtServiceMock.getClaims(any()))
//                .thenReturn(mock(Claims.class));
//        Mockito.doThrow(RuntimeException.class)
//                .when(authoritiesRetrieverMock)
//                .retrieveAuthorities();
//        // when
//        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);
//        // then
//        Assertions.assertThrows(AuthoritiesRetrieverException.class, executable);
//        Assertions.assertNull(MDC.get(MDC_UID));
//        verify(jwtServiceMock, Mockito.times(1))
//                .getClaims(token);
//        verify(authoritiesRetrieverMock, Mockito.times(1))
//                .retrieveAuthorities();
//        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
//    }


//    @Test
//    void authenticate_nullUidAndNullAuthorities() {
//        // given
//        String token = "token";
//        Authentication authentication = new JwtAuthenticationToken(token);
//        when(jwtServiceMock.getClaims(any()))
//                .thenReturn(mock(Claims.class));
//        // when
//        Authentication authenticate = jwtAuthenticationProvider.authenticate(authentication);
//        // then
//        Assertions.assertNull(MDC.get(MDC_UID));
//        Assertions.assertNotNull(authenticate);
//        assertEquals(token, authenticate.getCredentials());
//        assertEquals(SelfCareUser.class, authenticate.getPrincipal().getClass());
//        assertEquals("uid_not_provided", ((SelfCareUser) authenticate.getPrincipal()).getId());
//        Assertions.assertNotNull(authenticate.getAuthorities());
//        Assertions.assertTrue(authenticate.getAuthorities().isEmpty());
//        verify(jwtServiceMock, Mockito.times(1))
//                .getClaims(token);
//        verify(authoritiesRetrieverMock, Mockito.times(1))
//                .retrieveAuthorities();
//        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
//    }


//    @Test
//    void authenticate() {
//        // given
//        String token = "token";
//        Authentication authentication = new JwtAuthenticationToken(token);
//        String uid = "uid";
//        String email = "email@prova.com";
//        when(jwtServiceMock.getClaims(any()))
//                .thenReturn(new DefaultClaims(Map.of(CLAIM_UID, uid, CLAIM_EMAIL, email)));
//        String role = "role";
//        when(authoritiesRetrieverMock.retrieveAuthorities())
//                .thenReturn(List.of(new SimpleGrantedAuthority(role), new SimpleGrantedAuthority(role), new SimpleGrantedAuthority(role)));
//        // when
//        Authentication authenticate = jwtAuthenticationProvider.authenticate(authentication);
//        // then
//        assertEquals(uid, MDC.get(MDC_UID));
//        Assertions.assertNotNull(authenticate);
//        assertEquals(token, authenticate.getCredentials());
//        assertEquals(SelfCareUser.class, authenticate.getPrincipal().getClass());
//        assertEquals(uid, ((SelfCareUser) authenticate.getPrincipal()).getId());
//        assertEquals(email, ((SelfCareUser) authenticate.getPrincipal()).getEmail());
//        Assertions.assertNotNull(authenticate.getAuthorities());
//        assertEquals(3, authenticate.getAuthorities().size());
//        authenticate.getAuthorities().forEach(grantedAuthority -> assertEquals(role, grantedAuthority.getAuthority()));
//        verify(jwtServiceMock, Mockito.times(1))
//                .getClaims(token);
//        verify(authoritiesRetrieverMock, Mockito.times(1))
//                .retrieveAuthorities();
//        Mockito.verifyNoMoreInteractions(jwtServiceMock, authoritiesRetrieverMock);
//    }

    @Test
    void authenticate2() {
        // given
        String token = "token";
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token);
        jwtAuthenticationToken.setDetails("details");
        final JwtAuthenticationStrategy jwtAuthenticationStrategyMock = mock(JwtAuthenticationStrategy.class);
        when(jwtAuthenticationStrategyMock.authenticate(any()))
                .thenReturn(new JwtAuthenticationToken(null, null, null));
        when(jwtAuthenticationStrategyFactoryMock.create(any()))
                .thenReturn(jwtAuthenticationStrategyMock);
        // when
        final Authentication authentication = jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
        // then
        assertNotNull(authentication);
        assertEquals(jwtAuthenticationToken.getDetails(), authentication.getDetails());
        verify(jwtAuthenticationStrategyFactoryMock, times(1))
                .create(token);
        verify(jwtAuthenticationStrategyMock, times(1))
                .authenticate(jwtAuthenticationToken);
        verifyNoMoreInteractions(jwtAuthenticationStrategyFactoryMock, jwtAuthenticationStrategyMock);
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
