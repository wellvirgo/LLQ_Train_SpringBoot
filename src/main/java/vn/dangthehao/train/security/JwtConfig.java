package vn.dangthehao.train.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
  @Value("${rsa.private-key}")
  private String rsaPrivateKey;

  @Value("${rsa.public-key}")
  private String rsaPublicKey;

  @Bean
  public RSAPublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    String cleanedKey =
        rsaPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
    byte[] encodedKey = Base64.getDecoder().decode(cleanedKey);
    KeyFactory keyFactory = getKeyFactory();
    KeySpec keySpec = new X509EncodedKeySpec(encodedKey);

    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
  }

  @Bean
  public RSAPrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    String cleanedKey =
        rsaPrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
    byte[] encodedKey = Base64.getDecoder().decode(cleanedKey);
    KeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
    KeyFactory keyFactory = getKeyFactory();
    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
  }

  @Bean
  JwtDecoder getJwtDecoder(RSAPublicKey rsaPublicKey) {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }

  @Bean
  JwtEncoder getJwtEncoder(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
    JWK jwk = new RSAKey.Builder(rsaPublicKey).privateKey(rsaPrivateKey).build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  private KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
    String rsaAlgorithm = "RSA";
    return KeyFactory.getInstance(rsaAlgorithm);
  }
}
