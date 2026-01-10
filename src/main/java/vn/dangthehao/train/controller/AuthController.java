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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    AuthResponse authResponse =
        AuthResponse.builder().accessToken(tokenPairs.get(TokenKey.ACCESS_TOKEN.name())).build();

    ResponseCookie cookie =
        ResponseCookie.from(
                TokenKey.REFRESH_TOKEN.name(), tokenPairs.get(TokenKey.REFRESH_TOKEN.name()))
            .httpOnly(true)
            .secure(false)
            .path("api/auth/refresh")
            .maxAge(refreshTokenAliveTime)
            .sameSite("Lax")
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponseBuilder.success(authResponse));
  }
}
