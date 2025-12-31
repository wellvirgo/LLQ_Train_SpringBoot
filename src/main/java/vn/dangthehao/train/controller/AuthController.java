package vn.dangthehao.train.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.train.dto.auth.AuthRequest;
import vn.dangthehao.train.dto.auth.AuthResponse;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.security.service.AuthService;
import vn.dangthehao.train.util.ApiResponseBuilder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
  AuthService authService;

  @PostMapping
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
    AuthResponse authResponse =
        AuthResponse.builder().accessToken(authService.authenticate(request)).build();

    return ResponseEntity.ok(ApiResponseBuilder.success(authResponse));
  }
}
