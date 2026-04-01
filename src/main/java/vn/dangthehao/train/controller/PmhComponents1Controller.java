package vn.dangthehao.train.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.dangthehao.train.dto.common.ImportExcelResponse;
import vn.dangthehao.train.dto.component.request.BatchingUpdateStatusRequest;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.ComponentStatusResponse;
import vn.dangthehao.train.dto.component.response.FullDetailComponentResponse;
import vn.dangthehao.train.dto.component.response.PmhComponentResponse;
import vn.dangthehao.train.dto.component.response.SearchPmhComponentResponse;
import vn.dangthehao.train.service.export.ExportExcelService;
import vn.dangthehao.train.service.export.ExportJobService;
import vn.dangthehao.train.service.imports.ExcelImportComponentService;
import vn.dangthehao.train.service.pmhComponents1.PmhComponents1Service;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/pmh-components")
public class PmhComponents1Controller {
  PmhComponents1Service pmhComponents1Service;
  ExcelImportComponentService excelImportComponentService;
  ExportJobService exportJobService;
  ExportExcelService exportExcelService;

  @PostMapping("/search")
  public ResponseEntity<ApiResponse<SearchPmhComponentResponse>> searchComponent(
      @Valid @RequestBody SearchPmhComponentRequest request) {
    log.info("Page: {}", request.getPage());
    log.info("Size: {}", request.getSize());
    ApiResponse<SearchPmhComponentResponse> response =
        ApiResponseBuilder.success(pmhComponents1Service.searchComponent(request));

    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PmhComponentResponse>> createComponent(
      @Valid @RequestBody CreatePmhComponentRequest request) {
    ApiResponse<PmhComponentResponse> response =
        ApiResponseBuilder.success(pmhComponents1Service.createComponent(request));

    URI location = getComponentUri(response.getData().getId());

    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<PmhComponentResponse>> updateComponent(
      @PathVariable(name = "id") Long id, @Valid @RequestBody UpdatePmhComponentRequest request) {
    ApiResponse<PmhComponentResponse> response =
        ApiResponseBuilder.success(pmhComponents1Service.updateComponent(id, request));

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteComponentById(@PathVariable(name = "id") Long id) {
    pmhComponents1Service.deleteComponentById(id);
    return ResponseEntity.ok(ApiResponseBuilder.success());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<FullDetailComponentResponse>> getById(
      @PathVariable(name = "id") Long id) {
    try {
      Thread.sleep(1000);
      return ResponseEntity.ok(
          ApiResponseBuilder.success(pmhComponents1Service.getComponentById(id)));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/export")
  public ResponseEntity<ApiResponse<Long>> export(
      @Valid @RequestBody SearchPmhComponentRequest request, @AuthenticationPrincipal Jwt jwt) {
    String username = jwt.getSubject();
    Long exportJobId = exportJobService.createExportJob(username, request).getId();
    exportExcelService.exportComponentsAsync(exportJobId, request);

    return ResponseEntity.accepted().body(ApiResponseBuilder.success(exportJobId));
  }

  @PostMapping("/import")
  public ResponseEntity<ApiResponse<ImportExcelResponse>> importComponents(
      @RequestPart(name = "excel") MultipartFile file) {
    ImportExcelResponse response = excelImportComponentService.importComponents(file);
    return ResponseEntity.ok(ApiResponseBuilder.success(response));
  }

  @GetMapping("statuses")
  public ResponseEntity<ApiResponse<List<ComponentStatusResponse>>> getAllStatuses() {
    ApiResponse<List<ComponentStatusResponse>> response =
        ApiResponseBuilder.success(pmhComponents1Service.getAllStatuses());
    return ResponseEntity.ok(response);
  }

  @PatchMapping("statuses")
  public ResponseEntity<ApiResponse<Integer>> batchUpdateStatus(
      @RequestBody BatchingUpdateStatusRequest request) {
    int result = pmhComponents1Service.updateComponentStatus(request.getIds(), request.getStatus());

    return ResponseEntity.ok(ApiResponseBuilder.success(result));
  }

  private URI getComponentUri(Long id) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();
  }
}
