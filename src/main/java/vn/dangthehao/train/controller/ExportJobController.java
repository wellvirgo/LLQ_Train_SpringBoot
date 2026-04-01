package vn.dangthehao.train.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.dto.common.FileStreamResult;
import vn.dangthehao.train.service.export.ExportJobService;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/export-jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportJobController {
  ExportJobService exportJobService;

  @GetMapping("/{id}/status")
  public ResponseEntity<ApiResponse<Map<String, String>>> getStatusById(@PathVariable Long id) {
    String status = exportJobService.getStatusById(id);
    Map<String, String> map = new HashMap<>();
    map.put("status", status);
    return ResponseEntity.ok().body(ApiResponseBuilder.success(map));
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<StreamingResponseBody> downloadExcelFile(@PathVariable Long id) {
    FileStreamResult result = exportJobService.streamZippedFile(id);

    String filename = result.getFileName();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("application/zip"));
    String headerValue = "attachment; filename=\"" + filename + "\"";
    headers.set(HttpHeaders.CONTENT_DISPOSITION, headerValue);

    return ResponseEntity.ok().headers(headers).body(result.stream());
  }
}
