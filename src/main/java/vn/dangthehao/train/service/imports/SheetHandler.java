package vn.dangthehao.train.service.imports;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import vn.dangthehao.train.dto.component.FailedImportRow;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentStatus;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Getter
public class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
  private final List<PmhComponents1> components = new ArrayList<>();
  private final List<FailedImportRow> errors = new ArrayList<>();
  private int currentRow;
  private PmhComponents1 currentComponent;
  private boolean isValidRow;
  private FailedImportRow currentFailedRow;
  private static final int HEADER_ROW_INDEX = 0;
  private static final String ERROR_DETAIL_PATTERN = "Column %s: %s";

  private final Consumer<List<PmhComponents1>> saveProcessor;

  public SheetHandler(Consumer<List<PmhComponents1>> saveProcessor) {
    this.saveProcessor = saveProcessor;
  }

  @Override
  public void startRow(int row) {
    if (row > HEADER_ROW_INDEX) {
      currentComponent = new PmhComponents1();
      currentFailedRow = new FailedImportRow();
      this.currentRow = row;
      this.isValidRow = true;
    }
  }

  @Override
  public void endRow(int row) {
    if (row <= HEADER_ROW_INDEX || currentComponent == null) return;

    if (isInvalidCrossDate(
        currentComponent.getEffectiveDate(), currentComponent.getEndEffectiveDate()))
      addError("D", "End effective date occurs before the start effective date");

    if (!this.isValidRow) {
      errors.add(currentFailedRow);
      return;
    }

    components.add(currentComponent);
    if (this.components.size() == 1000) {
      this.saveProcessor.accept(components);
      this.components.clear();
    }
  }

  @Override
  public void cell(String cellRef, String formattedVal, XSSFComment xssfComment) {
    if (currentComponent == null) return;

    String colIndex = getColLetter(cellRef);
    switch (colIndex) {
      case "A" -> {
        this.currentFailedRow.setComponentCode(formattedVal);
        if (isInvalidStringField(formattedVal, true, 20)) {
          addError(colIndex, "Component code is blank or exceeds 20 characters");
          return;
        }
        currentComponent.setComponentCode(formattedVal);
      }
      case "B" -> {
        this.currentFailedRow.setComponentName(formattedVal);
        if (isInvalidStringField(formattedVal, true, 150)) {
          addError(colIndex, "Component name is blank or exceeds 150 characters");
          return;
        }
        currentComponent.setComponentName(formattedVal);
      }
      case "C" -> {
        this.currentFailedRow.setEffectiveDate(formattedVal);
        LocalDate effectiveDate = validateDateString(formattedVal, true, colIndex);
        currentComponent.setEffectiveDate(effectiveDate);
      }
      case "D" -> {
        this.currentFailedRow.setEndEffectiveDate(formattedVal);
        LocalDate endEffectiveDate = validateDateString(formattedVal, false, colIndex);
        currentComponent.setEndEffectiveDate(endEffectiveDate);
      }
      case "E" -> {
        this.currentFailedRow.setMessageType(formattedVal);
        if (isInvalidStringField(formattedVal, false, 1500)) {
          addError(colIndex, "Message type is exceeds 1500 characters");
          return;
        }
        currentComponent.setMessageType(formattedVal);
      }
      case "F" -> {
        this.currentFailedRow.setConnectionMethod(formattedVal);
        if (isInvalidStringField(formattedVal, false, 1000)) {
          addError(colIndex, "Connection Method is exceeds 1000 characters");
          return;
        }
        currentComponent.setConnectionMethod(formattedVal);
      }
      case "G" -> {
        this.currentFailedRow.setStatus(formattedVal);
        if (ComponentStatus.getValueByLabel(formattedVal) == null) {
          addError(colIndex, "Component status is invalid");
          return;
        }
        currentComponent.setStatus(ComponentStatus.getValueByLabel(formattedVal));
      }
    }
  }

  public void processRemaining() {
    if (this.components.isEmpty()) return;
    this.saveProcessor.accept(components);
  }

  private String getColLetter(String cellRef) {
    if (cellRef == null || cellRef.isBlank()) return "";
    return cellRef.replaceAll("\\d+", "");
  }

  private boolean isInvalidStringField(String value, boolean required, int maxLength) {
    if (required) {
      return value == null || value.isBlank() || value.length() > maxLength;
    }
    return value != null && value.length() > maxLength;
  }

  private LocalDate validateDateString(String dateString, boolean required, String colIndex) {
    try {
      LocalDate date = LocalDate.parse(dateString);
      if (isInvalidDateField(date, required)) {
        throw new AppException(ErrorCode.DATE_IN_PAST);
      }
      return date;
    } catch (DateTimeParseException | NullPointerException e) {
      addError(colIndex, "Invalid date format");
    } catch (AppException appEx) {
      addError(colIndex, appEx.getErrorCode().getMessage());
    }

    return null;
  }

  private boolean isInvalidDateField(LocalDate localDate, boolean required) {
    if (required && localDate == null) return true;
    if (localDate == null) return false;
    return localDate.isBefore(LocalDate.now());
  }

  private boolean isInvalidCrossDate(LocalDate startDate, LocalDate endDate) {
    if (startDate == null && endDate == null) return false;
    if (endDate == null) return false;
    return endDate.isBefore(startDate);
  }

  private void addError(String colIndex, String error) {
    String errorMessage = String.format(ERROR_DETAIL_PATTERN, colIndex, error);
    this.currentFailedRow.appendError(errorMessage);
    this.isValidRow = false;
  }
}
