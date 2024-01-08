package it.pagopa.selfcare.pagopa.backoffice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.JWT_PROD_ISSUER;

/**
 * Common helper methods to work with JWT
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * the public key for dev or uat
     */
    private final PublicKey jwtSigningKey;

    /**
     * the public key for production (we need both because we can accept both on the same environment)
     */
    private final PublicKey jwtSigningKeyProd;


    public JwtUtil(@Value("${jwt.jwtSigningKey}") String jwtSigningKey, @Value("${jwt.jwtsigningKeyProd}") String jwtSigningKeyProd) throws Exception {
        this.jwtSigningKey = getPublicKey(jwtSigningKey);
        this.jwtSigningKeyProd = getPublicKey(jwtSigningKeyProd);
    }

    /**
     * The application can accept JWT that they are signed by PROD or UAT issuers.
     * To use the right public key we need to check the issuer
     *
     * @param token the JWT from the request
     * @return the claim from the JWT
     */
    public Claims getClaims(JwtAuthenticationToken token) {
        PublicKey publicKey;
        boolean isProd = isProdIssuerFromJWT(token);
        if(isProd) {
            publicKey = jwtSigningKeyProd;
        } else {
            publicKey = jwtSigningKey;
        }

        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.getCredentials())
                .getBody();
    }


    /**
     * @param request The HTTP request
     * @return the JWT passed in the Authorization header as Bearer
     */
    public static String getJwtFromRequest(HttpServletRequest request) {
        String jwt = null;
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        return jwt;
    }

    /**
     * @param authentication the JWT from the request
     * @return true if the issuer is equals to 'https://api.platform.pagopa.it'
     */
    @SuppressWarnings("java:S5659")
    private static boolean isProdIssuerFromJWT(JwtAuthenticationToken authentication) {
        String jwt = authentication.getCredentials();
        if(jwt == null) {
            return false;
        }
        String issuer = ((DefaultClaims) (Jwts.parser().parse(jwt.substring(0, jwt.lastIndexOf('.') + 1)).getBody())).getIssuer();
        return JWT_PROD_ISSUER.equals(issuer);
    }


    /**
     * @param signingKey The public key as string
     * @return the PublicKey object
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PublicKey getPublicKey(String signingKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = signingKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getMimeDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

}
