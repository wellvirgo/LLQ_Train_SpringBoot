package vn.dangthehao.train.service.export;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentCellHeader;
import vn.dangthehao.train.enums.ComponentStatus;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ExcelExportComponentService {

  public void export(HttpServletResponse response, List<PmhComponents1> components) {
    try (Workbook workbook = new XSSFWorkbook()) {
      int CURRENT_SHEET_INDEX = 1;
      Sheet sheet = workbook.createSheet("Sheet" + CURRENT_SHEET_INDEX);

      createHeaderRow(workbook, sheet);
      createSheetRows(workbook, sheet, components);

      final String fileName = "export_" + System.currentTimeMillis() + ".xlsx";
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }

  private void createSheetRows(Workbook workbook, Sheet sheet, List<PmhComponents1> components) {
    CellStyle dateStyle = workbook.createCellStyle();
    CreationHelper createHelper = workbook.getCreationHelper();
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

    int rowNum = 1;
    for (PmhComponents1 component : components) {
      int cellNum = 0;
      Row row = sheet.createRow(rowNum++);
      row.createCell(cellNum++).setCellValue(component.getComponentCode());
      row.createCell(cellNum++).setCellValue(component.getComponentName());

      Cell effectiveDateCell = row.createCell(cellNum++);
      effectiveDateCell.setCellStyle(dateStyle);
      effectiveDateCell.setCellValue(component.getEffectiveDate());

      Cell endEffectiveDateCell = row.createCell(cellNum++);
      endEffectiveDateCell.setCellStyle(dateStyle);
      endEffectiveDateCell.setCellValue(component.getEndEffectiveDate());

      row.createCell(cellNum++).setCellValue(component.getMessageType());
      row.createCell(cellNum++).setCellValue(component.getConnectionMethod());
      row.createCell(cellNum)
          .setCellValue(ComponentStatus.getLabelByValue(component.getStatus()));
    }
  }

  private void createHeaderRow(Workbook workbook, Sheet sheet) {
    Row header = sheet.createRow(0);

    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFont(headerFont);

    int cellIndex = 0;
    Cell headerCell = header.createCell(cellIndex);
    for (ComponentCellHeader cellHeaderValue : ComponentCellHeader.values()) {
      createHeaderCell(headerCell, headerStyle, cellHeaderValue.value());
      headerCell = header.createCell(++cellIndex);
    }
  }

  private void createHeaderCell(Cell cell, CellStyle headerStyle, String value) {
    cell.setCellValue(value);
    cell.setCellStyle(headerStyle);
  }
}
