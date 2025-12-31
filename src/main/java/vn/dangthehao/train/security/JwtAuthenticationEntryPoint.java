package vn.dangthehao.train.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
    ApiResponse<Void> apiResponse =
        ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), null);
    objectMapper.writeValue(response.getOutputStream(), apiResponse);
  }
}
