package vn.dangthehao.train.security.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.entity.AppUser;
import vn.dangthehao.train.entity.CpnRefreshToken;
import vn.dangthehao.train.repository.RefreshTokenRepository;
import vn.dangthehao.train.repository.UserRepository;
import vn.dangthehao.train.security.CustomUserDetails;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RefreshTokenService {
  UserRepository userRepository;
  RefreshTokenRepository refreshTokenRepository;

  @NonFinal
  @Value("${jwt.refresh-token-alive-time}")
  Long aliveTime;

  public void saveRefreshToken(Authentication authentication, String token) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    AppUser user = userRepository.getReferenceById(userDetails.getId());

    long aliveTimeDay = Duration.ofSeconds(aliveTime).toDays();

    CpnRefreshToken refreshToken =
        CpnRefreshToken.builder()
            .token(token)
            .user(user)
            .expiredAt(LocalDate.now().plusDays(aliveTimeDay))
            .isRevoked(false)
            .build();
    refreshTokenRepository.save(refreshToken);
  }
}
