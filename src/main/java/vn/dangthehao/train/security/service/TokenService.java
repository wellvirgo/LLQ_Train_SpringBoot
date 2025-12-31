package vn.dangthehao.train.security.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class TokenService {
  JwtEncoder jwtEncoder;

  @NonFinal
  @Value("${jwt.alive-time}")
  Long aliveTime;

  public String generateToken(Authentication authentication) {
    String scope = extractScope(authentication);
    Instant issuedAt = Instant.now();

    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .issuer("localhost")
            .issuedAt(Instant.now())
            .subject(authentication.getName())
            .expiresAt(issuedAt.plus(aliveTime, ChronoUnit.SECONDS))
            .claim("scope", scope)
            .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }

  private String extractScope(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(" "));
  }
}
