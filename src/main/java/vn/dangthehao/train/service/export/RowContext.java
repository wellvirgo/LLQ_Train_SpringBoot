package vn.dangthehao.train.service.export;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RowContext {
  final CellStyle dateCellStyle;
  final CellStyle errorCellStyle;
  @Setter Row currentRow;
  int cellNum;

  public RowContext(CellStyle dateCellStyle, CellStyle errorCellStyle) {
    this.dateCellStyle = dateCellStyle;
    this.errorCellStyle = errorCellStyle;
    this.cellNum = 0;
  }

  public void resetCellNum() {
    this.cellNum = 0;
  }

  public void createCell(String value, CellStyle style) {
    Cell cell = this.currentRow.createCell(cellNum++);
    cell.setCellValue(value);
    if (style != null) cell.setCellStyle(style);
  }

  public void createDateCell(LocalDate value) {
    Cell cell = this.currentRow.createCell(cellNum++);
    cell.setCellValue(value);
    cell.setCellStyle(this.dateCellStyle);
  }

  public void createErrorCell(String value) {
    Cell cell = this.currentRow.createCell(cellNum++);
    cell.setCellValue(value);
    cell.setCellStyle(this.errorCellStyle);
  }
}
