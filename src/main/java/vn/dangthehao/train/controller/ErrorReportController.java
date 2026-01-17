package vn.dangthehao.train.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.train.service.imports.ExcelImportComponentService;

@RestController
@RequestMapping("/api/error-reports/{name}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorReportController {
  ExcelImportComponentService excelImportComponentService;

  @GetMapping()
  public ResponseEntity<Resource> downloadErrorReport(@PathVariable String name) {
    Resource file = excelImportComponentService.loadErrorImportReport(name);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name)
        .body(file);
  }
}
