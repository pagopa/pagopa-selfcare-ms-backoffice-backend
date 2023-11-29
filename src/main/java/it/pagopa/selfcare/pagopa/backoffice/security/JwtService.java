package it.pagopa.selfcare.pagopa.backoffice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

    /**
     *  the public key for dev or uat
     */
    private final PublicKey jwtSigningKey;

    /**
     *  the public key for production (we need both because we can accept both on the same environment)
     */
    private final PublicKey jwtSigningKeyProd;


    public JwtService(@Value("${jwt.jwtSigningKey}") String jwtSigningKey, @Value("${jwt.jwtsigningKeyProd}") String jwtSigningKeyProd) throws Exception {
        this.jwtSigningKey = getPublicKey(jwtSigningKey);
        this.jwtSigningKeyProd = getPublicKey(jwtSigningKeyProd);
    }

    /**
     * The application can accept JWT that they are signed by PROD or UAT issuers.
     * To use the right public key we need to check the issuer and pass as flag {@code isProd}
     *
     * @param token the JWT from the request
     * @param isProd true if the issuer is PROD
     * @return the claim from the JWT
     */
    public Claims getClaims(String token, boolean isProd) {
        PublicKey publicKey;
        if (isProd) {
            publicKey = jwtSigningKeyProd;
        } else {
            publicKey = jwtSigningKey;
        }

        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
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
