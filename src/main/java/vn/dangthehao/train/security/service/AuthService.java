package vn.dangthehao.train.security.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.dto.auth.AuthRequest;
import vn.dangthehao.train.enums.TokenKey;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {
  TokenService tokenService;
  RefreshTokenService refreshTokenService;
  UserDetailsService userDetailsService;
  AuthenticationManager authenticationManager;

  @Transactional
  public Map<String, String> authenticate(AuthRequest authRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword()));

    String accessToken = tokenService.generateToken(authentication, true);
    String refreshToken = tokenService.generateToken(authentication, false);

    refreshTokenService.saveRefreshToken(authentication, refreshToken);

    return Map.of(
        TokenKey.ACCESS_TOKEN.name(), accessToken, TokenKey.REFRESH_TOKEN.name(), refreshToken);
  }

  @Transactional
  public Map<String, String> renewTokens(String refreshToken) {
    if (!refreshTokenService.validateToken(refreshToken)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    String userName = tokenService.extractUsername(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    String accessToken = tokenService.generateToken(authentication, true);
    String newRefreshToken = tokenService.generateToken(authentication, false);

    refreshTokenService.revokeRefreshToken(refreshToken);
    refreshTokenService.saveRefreshToken(authentication, newRefreshToken);

    return Map.of(
        TokenKey.ACCESS_TOKEN.name(), accessToken, TokenKey.REFRESH_TOKEN.name(), newRefreshToken);
  }

  @Transactional
  public void logout(String refreshToken) {
    refreshTokenService.revokeRefreshToken(refreshToken);
  }
}
