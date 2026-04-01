package vn.dangthehao.train.dto.common;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record FileStreamResult(String filename, StreamingResponseBody stream) {
  public String getFileName() {
    if (filename == null) return "download.zip";
    return filename;
  }
}
