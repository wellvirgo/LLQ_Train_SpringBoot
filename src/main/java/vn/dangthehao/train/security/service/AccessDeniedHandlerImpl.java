package vn.dangthehao.train.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
  ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErrorCode errorCode = ErrorCode.FORBIDDEN;
    ApiResponse<Void> apiResponse =
        ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), null);

    objectMapper.writeValue(response.getOutputStream(), apiResponse);
  }
}
