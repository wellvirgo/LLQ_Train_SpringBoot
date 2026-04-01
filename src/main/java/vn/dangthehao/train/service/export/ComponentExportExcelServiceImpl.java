package vn.dangthehao.train.service.export;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.dto.component.DraftImportRow;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentCellHeader;
import vn.dangthehao.train.enums.ComponentStatus;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.service.pmhComponents1.dynamicSearch.ProcedureSearch;
import vn.dangthehao.train.service.pmhComponents1.dynamicSearch.SearchComponentFactory;
import vn.dangthehao.train.service.pmhComponents1.dynamicSearch.SearchComponentService;
import vn.dangthehao.train.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ComponentExportExcelServiceImpl implements ExportExcelService {
  ExportJobService exportJobService;
  SearchComponentService searchComponentService;

  static int MAX_ROWS_PER_SHEET = 500;
  static long CHUNK_SIZE = 1000;

  public ComponentExportExcelServiceImpl(
      ExportJobService exportJobService, SearchComponentFactory searchComponentFactory) {
    this.exportJobService = exportJobService;
    this.searchComponentService = searchComponentFactory.getSearchService(ProcedureSearch.class);
  }

  @Async
  @Transactional
  public void exportComponentsAsync(Long jobId, SearchPmhComponentRequest request) {
    this.export(
        jobId, "export_component", ComponentCellHeader.values(), request, this::fillComponentRow);
  }

  public Path exportFailedImport(List<DraftImportRow> failedRows) {
    ComponentCellHeader[] componentCellHeaders = ComponentCellHeader.values();
    String[] headers = new String[componentCellHeaders.length + 1];
    int i = 0;
    for (; i < componentCellHeaders.length; i++) {
      headers[i] = componentCellHeaders[i].value();
    }
    headers[i] = "Error Details";
    return this.export("export_failed", headers, failedRows, this::fillFailedImportingRow);
  }

  @SuppressWarnings("SameParameterValue")
  public void export(
      Long jobId,
      String filenamePrefix,
      Object headers,
      SearchPmhComponentRequest criteria,
      BiConsumer<RowContext, PmhComponents1> rowFill) {
    this.exportJobService.updateStatus(jobId, "PROCESSING");

    try {
      Thread.sleep(Duration.of(10, ChronoUnit.SECONDS));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    Path tempFilePath = FileUtils.createTempFile(filenamePrefix, ".xlsx", "excel");

    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        OutputStream out =
            Files.newOutputStream(
                tempFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dateStyle = createDateStyle(workbook);
      RowContext rowContext = new RowContext(dateStyle);

      int sheetIndex = 1;
      int rowIndex = 1;
      int page = 1;
      boolean hasMoreDate = true;
      Sheet currentSheet = createNewSheet(workbook, sheetIndex, headers, headerStyle);

      while (hasMoreDate) {
        criteria.setPage(page);
        criteria.setSize(CHUNK_SIZE);

        List<PmhComponents1> data = searchComponentService.search(criteria).getData();
        if (data.isEmpty()) {
          hasMoreDate = false;
        }

        for (PmhComponents1 item : data) {
          if (rowIndex > MAX_ROWS_PER_SHEET) {
            sheetIndex++;
            currentSheet = createNewSheet(workbook, sheetIndex, headers, dateStyle);
            rowIndex = 1;
          }

          rowContext.setCurrentRow(currentSheet.createRow(rowIndex++));
          rowContext.resetCellNum();
          rowFill.accept(rowContext, item);
        }
        page++;
      }

      workbook.write(out);
      String finalFilename = tempFilePath.getFileName().toString();
      exportJobService.completeExportJob(
          jobId, finalFilename, "http://localhost:8080/temp/excel/" + finalFilename);

    } catch (IOException e) {
      log.error("Failed to export excel file for JobId {}", jobId, e);
      exportJobService.failExportJob(jobId);
    }
  }

  private Sheet createNewSheet(
      Workbook workbook, int sheetIndex, Object headers, CellStyle headerStyle) {
    Sheet sheet = workbook.createSheet("Sheet" + sheetIndex);

    if (headers instanceof ComponentCellHeader[]) {
      createHeaderRow(sheet, headerStyle, (ComponentCellHeader[]) headers);
    } else {
      createHeaderRow(sheet, headerStyle, (String[]) headers);
    }

    return sheet;
  }

  @SuppressWarnings("SameParameterValue")
  private <T> Path export(
      String prefix, Object headers, List<T> data, BiConsumer<RowContext, T> rowFill) {
    final String targetDir = "excel";
    final String extension = ".xlsx";
    try (Workbook workbook = new XSSFWorkbook()) {
      createFileContent(workbook, headers, data, rowFill);
      Path tempFile = FileUtils.createTempFile(prefix, extension, targetDir);

      try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(tempFile))) {
        workbook.write(bos);
      }
      return tempFile;
    } catch (IOException e) {
      log.info(e.getMessage());
      throw new AppException(ErrorCode.UNABLE_CREATE_FILE, e.getMessage());
    }
  }

  private <T> void createFileContent(
      Workbook workbook, Object headers, List<T> data, BiConsumer<RowContext, T> rowFill) {
    Sheet sheet = workbook.createSheet("Sheet1");

    CellStyle headerStyle = createHeaderStyle(workbook);
    CellStyle dateStyle = createDateStyle(workbook);
    CellStyle errorStyle = createErrorStyle(workbook);

    if (headers instanceof ComponentCellHeader[]) {
      createHeaderRow(sheet, headerStyle, (ComponentCellHeader[]) headers);
    } else {
      createHeaderRow(sheet, headerStyle, (String[]) headers);
    }

    RowContext context = new RowContext(dateStyle, errorStyle);
    int rowIndex = 1;
    for (T item : data) {
      context.setCurrentRow(sheet.createRow(rowIndex++));
      context.resetCellNum();
      rowFill.accept(context, item);
    }
  }

  private void fillComponentRow(RowContext context, PmhComponents1 component) {
    context.createCell(component.getComponentCode(), null);
    context.createCell(component.getComponentName(), null);
    context.createDateCell(component.getEffectiveDate());
    context.createDateCell(component.getEndEffectiveDate());
    context.createCell(component.getMessageType(), null);
    context.createCell(component.getConnectionMethod(), null);
    context.createCell(ComponentStatus.getLabelByValue(component.getStatus()), null);
  }

  private void fillFailedImportingRow(RowContext context, DraftImportRow failedRow) {
    context.createCell(failedRow.getComponentCode(), null);
    context.createCell(failedRow.getComponentName(), null);
    context.createCell(failedRow.getEffectiveDate(), null);
    context.createCell(failedRow.getEndEffectiveDate(), null);
    context.createCell(failedRow.getMessageType(), null);
    context.createCell(failedRow.getConnectionMethod(), null);
    context.createCell(failedRow.getStatus(), null);
    context.createErrorCell(failedRow.getErrorDetails().toString());
  }

  private void createHeaderRow(Sheet sheet, CellStyle style, ComponentCellHeader[] headers) {
    Row row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = row.createCell(i);
      cell.setCellStyle(style);
      cell.setCellValue(headers[i].value());
    }
  }

  private void createHeaderRow(Sheet sheet, CellStyle style, String[] headers) {
    Row row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = row.createCell(i);
      cell.setCellStyle(style);
      cell.setCellValue(headers[i]);
    }
  }

  private static CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);

    return style;
  }

  private static CellStyle createDateStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    CreationHelper createHelper = workbook.getCreationHelper();
    style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

    return style;
  }

  private static CellStyle createErrorStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setColor(IndexedColors.RED.getIndex());
    style.setFont(font);

    return style;
  }
}
