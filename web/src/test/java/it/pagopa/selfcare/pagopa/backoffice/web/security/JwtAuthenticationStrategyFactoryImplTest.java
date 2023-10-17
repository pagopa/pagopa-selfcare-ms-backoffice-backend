package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JwtAuthenticationStrategyFactoryImplTest {

    private final JwtAuthenticationStrategyFactoryImpl jwtAuthenticationStrategyFactory;
    private final BeanFactory beanFactoryMock;
    public JwtAuthenticationStrategyFactoryImplTest() {
        beanFactoryMock = mock(BeanFactory.class);
        Mockito.when(beanFactoryMock.getBean(anyString(), any(Class.class)))
                .thenAnswer(invocation -> {
                    final JwtAuthenticationStrategy jwtAuthenticationStrategy;
                    final Class<?> argument = invocation.getArgument(1, Class.class);
                    if (PagopaAuthenticationStrategy.class.equals(argument)) {
                        jwtAuthenticationStrategy = new PagopaAuthenticationStrategy(
                                mock(JwtService.class),
                                mock(AuthoritiesRetriever.class)
                        );

                    } else if (PagopaPRODAuthenticationStrategy.class.equals(argument)) {
                        jwtAuthenticationStrategy = new PagopaPRODAuthenticationStrategy(
                                mock(JwtService.class),
                                mock(AuthoritiesRetriever.class)
                        );
                    } else {
                        jwtAuthenticationStrategy = null;
                    }
                    return jwtAuthenticationStrategy;
                });
        jwtAuthenticationStrategyFactory = new JwtAuthenticationStrategyFactoryImpl(beanFactoryMock);
    }


    @Test
    void create_JwtException() {
        // given
        final String jwt = null;
        // when
        final Executable executable = () -> jwtAuthenticationStrategyFactory.create(jwt);
        // then
        assertThrows(JwtAuthenticationException.class, executable);
        verifyNoInteractions(beanFactoryMock);
    }

    private static Stream<Arguments> getJwtAuthenticationStrategyArgumentsProvider() {
        return Stream.of(
                Arguments.of(PagopaAuthenticationStrategy.class, "SPID"),
                Arguments.of(PagopaPRODAuthenticationStrategy.class, "https://api.platform.pagopa.it")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getJwtAuthenticationStrategyArgumentsProvider")
    void create(Class<?> clazz, String issuer) throws Exception {
        // given
        final DefaultClaims claims = new DefaultClaims();
        claims.setIssuer(issuer);
        final String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, loadPrivateKey()).compact();
        // when
        final JwtAuthenticationStrategy jwtAuthenticationStrategy = jwtAuthenticationStrategyFactory.create(jwt);
        // then
        assertEquals(clazz, jwtAuthenticationStrategy.getClass());
        verify(beanFactoryMock, times(1))
                .getBean(anyString(), eq(clazz));
        verifyNoMoreInteractions(beanFactoryMock);
    }


    private PrivateKey loadPrivateKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:certs/key.pem");
        String key = Files.readString(file.toPath(), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getMimeDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

}
