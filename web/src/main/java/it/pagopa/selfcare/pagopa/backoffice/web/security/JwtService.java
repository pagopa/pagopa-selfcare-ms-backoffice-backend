package it.pagopa.selfcare.pagopa.backoffice.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Common helper methods to work with JWT
 */
@Slf4j
@Component
public class JwtService {

    private final PublicKey jwtSigningKey;
    private final PublicKey jwtSigningKeyProd;


    public JwtService(@Value("${jwt.jwtSigningKey}") String jwtSigningKey, @Value("${jwt.jwtsigningKeyProd}") String jwtSigningKeyProd) throws Exception {
        this.jwtSigningKey = getPublicKey(jwtSigningKey);
        this.jwtSigningKeyProd = getPublicKey(jwtSigningKeyProd);
    }


    public Claims getClaims(String token) {
        log.trace("getClaims start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getClaims token = {}", token);
        return Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getClaimsProd(String token) {
        log.trace("getClaims start");
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getClaims token = {}", token);
        return Jwts.parser()
                .setSigningKey(jwtSigningKeyProd)
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getClaims(String env, String token) {
        log.trace("getClaims start");
        Claims claims;
        if (env != null && env.equals("PROD")) {
            claims = getClaimsProd(token);
        } else {
            claims = getClaims(token);
        }
        return claims;
    }


    private PublicKey getPublicKey(String signingKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.trace("getPublicKey");
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
