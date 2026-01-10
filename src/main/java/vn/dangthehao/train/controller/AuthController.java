package vn.dangthehao.train.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.train.dto.auth.AuthRequest;
import vn.dangthehao.train.dto.auth.AuthResponse;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.enums.TokenKey;
import vn.dangthehao.train.security.service.AuthService;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
  AuthService authService;

  @NonFinal
  @Value("${jwt.refresh-token-alive-time}")
  Long refreshTokenAliveTime;

  @PostMapping
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
    Map<String, String> tokenPairs = authService.authenticate(request);
    return getAuthResponse(tokenPairs);
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken) {
    Map<String, String> tokenPairs = authService.renewTokens(refreshToken);
    return getAuthResponse(tokenPairs);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken) {
    log.info("{}", refreshToken);
    authService.logout(refreshToken);
    ResponseCookie cookie = buildRefreshTokenCookie(TokenKey.REFRESH_TOKEN.name(), "", 0);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponseBuilder.success());
  }

  private ResponseCookie buildRefreshTokenCookie(String key, String value, long maxAge) {
    return ResponseCookie.from(key, value)
        .httpOnly(true)
        .secure(false)
        .path("/api/auth")
        .maxAge(maxAge)
        .sameSite("Lax")
        .build();
  }

  private ResponseEntity<ApiResponse<AuthResponse>> getAuthResponse(
      Map<String, String> tokenPairs) {
    AuthResponse authResponse =
        AuthResponse.builder().accessToken(tokenPairs.get(TokenKey.ACCESS_TOKEN.name())).build();

    ResponseCookie cookie =
        buildRefreshTokenCookie(
            TokenKey.REFRESH_TOKEN.name(),
            tokenPairs.get(TokenKey.REFRESH_TOKEN.name()),
            refreshTokenAliveTime);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponseBuilder.success(authResponse));
  }
}
