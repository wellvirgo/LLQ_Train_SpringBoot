package vn.dangthehao.train.service.export;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.component.DraftImportRow;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentCellHeader;
import vn.dangthehao.train.enums.ComponentStatus;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ComponentExportExcelServiceImpl implements ExportExcelService {

  public void exportComponents(HttpServletResponse response, List<PmhComponents1> pmhComponents1) {
    this.export(
        response,
        "export_component",
        ComponentCellHeader.values(),
        pmhComponents1,
        this::fillComponentRow);
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
  private <T> void export(
      HttpServletResponse response,
      String filenamePrefix,
      Object headers,
      List<T> data,
      BiConsumer<RowContext, T> rowFill) {
    try (Workbook workbook = new XSSFWorkbook()) {
      createFileContent(workbook, headers, data, rowFill);

      String filename = filenamePrefix + "_" + System.currentTimeMillis() + ".xlsx";
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=" + filename);

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.info(e.getMessage());
    }
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
