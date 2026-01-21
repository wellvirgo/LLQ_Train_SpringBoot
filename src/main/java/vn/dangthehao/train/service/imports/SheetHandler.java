package vn.dangthehao.train.service.imports;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import vn.dangthehao.train.dto.component.DraftImportRow;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentCellHeader;
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
  private static final int HEADER_ROW_INDEX = 0;
  private static final String ERROR_DETAIL_PATTERN = "Column %s: %s";
  private static final int BATCH_SIZE = 1000;

  private final List<PmhComponents1> components;
  private final List<DraftImportRow> errors;
  private final Consumer<List<PmhComponents1>> saveProcessor;

  private int currentRow;
  private boolean isValidRow;
  private DraftImportRow draftRow;

  public SheetHandler(Consumer<List<PmhComponents1>> saveProcessor) {
    this.components = new ArrayList<>();
    this.errors = new ArrayList<>();
    this.saveProcessor = saveProcessor;
  }

  @Override
  public void startRow(int row) {
    if (row <= HEADER_ROW_INDEX) return;

    draftRow = new DraftImportRow();
    this.currentRow = row;
    this.isValidRow = true;
  }

  @Override
  public void endRow(int row) {
    if (row <= HEADER_ROW_INDEX) return;

    String componentCode = draftRow.getComponentCode();
    String componentName = draftRow.getComponentName();
    String messageType = draftRow.getMessageType();
    String connectionMethod = draftRow.getConnectionMethod();
    Long status = ComponentStatus.getValueByLabel(draftRow.getStatus());

    if (isInvalidStringField(componentCode, true, 20))
      addError("A", "Component code is blank or exceeds 20 characters");

    if (isInvalidStringField(componentName, true, 150))
      addError("B", "Component name is blank or exceeds 150 characters");

    LocalDate effectiveDate = validateDateString(draftRow.getEffectiveDate(), true, "C");

    LocalDate endEffectiveDate = null;
    if (draftRow.getEndEffectiveDate() != null)
      endEffectiveDate = validateDateString(draftRow.getEndEffectiveDate(), false, "C");

    if (isInvalidCrossDate(effectiveDate, endEffectiveDate))
      addError("D", "End effective date occurs before the start effective date");

    if (isInvalidStringField(messageType, false, 1500))
      addError("E", "Message type is exceeds 1500 characters");

    if (isInvalidStringField(connectionMethod, false, 1000))
      addError("F", "Connection Method is exceeds 1000 characters");

    if (status == null) addError("G", "Status is invalid");

    if (!this.isValidRow) {
      errors.add(draftRow);
      return;
    }

    PmhComponents1 component =
        PmhComponents1.builder()
            .componentCode(componentCode)
            .componentName(componentName)
            .effectiveDate(effectiveDate)
            .endEffectiveDate(endEffectiveDate)
            .messageType(messageType)
            .connectionMethod(connectionMethod)
            .status(status)
            .build();
   addValidComponent(component);
  }

  @Override
  public void cell(String cellRef, String formattedVal, XSSFComment xssfComment) {
    String colLetter = CellHandler.getColLetter(cellRef);
    if (currentRow == HEADER_ROW_INDEX) {
      this.validateTemplate(colLetter, formattedVal);
      return;
    }

    CellHandler.getCellData(colLetter, formattedVal, draftRow);
  }

  private void validateTemplate(String colLetter, String value) {
    switch (colLetter) {
      case "A" -> {
        if (!ComponentCellHeader.CODE.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "B" -> {
        if (!ComponentCellHeader.NAME.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "C" -> {
        if (!ComponentCellHeader.EFFECTIVE_DATE.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "D" -> {
        if (!ComponentCellHeader.END_EFFECTIVE_DATE.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "E" -> {
        if (!ComponentCellHeader.MESSAGE_TYPE.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "F" -> {
        if (!ComponentCellHeader.CONNECTION_METHOD.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
      case "G" -> {
        if (!ComponentCellHeader.STATUS.value().equals(value))
          throw new AppException(ErrorCode.INVALID_FILE_TEMPLATE);
      }
    }
  }

  private boolean isInvalidStringField(String value, boolean required, int maxLength) {
    if (required) {
      return value == null || value.isBlank() || value.length() > maxLength;
    }
    return value != null && value.length() > maxLength;
  }

  @SuppressWarnings("SameParameterValue")
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
    if (startDate == null || endDate == null) return false;
    return endDate.isBefore(startDate);
  }

  private void addError(String colIndex, String error) {
    String errorMessage = String.format(ERROR_DETAIL_PATTERN, colIndex, error);
    draftRow.appendError(errorMessage);
    isValidRow = false;
  }

  private void addValidComponent(PmhComponents1 component) {
    components.add(component);
    if (components.size() == BATCH_SIZE) {
      saveProcessor.accept(components);
      components.clear();
    }
  }
}
