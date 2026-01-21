package vn.dangthehao.train.service.imports;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.train.dto.component.DraftImportRow;

import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CellHandler {
  static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");

  public static String getColLetter(String cellRef) {
    return DIGITS_PATTERN.matcher(cellRef).replaceAll("");
  }

  public static void getCellData(String colLetter, String value, DraftImportRow draftRow) {
    switch (colLetter) {
      case "A" -> draftRow.setComponentCode(value);
      case "B" -> draftRow.setComponentName(value);
      case "C" -> draftRow.setEffectiveDate(value);
      case "D" -> draftRow.setEndEffectiveDate(value);
      case "E" -> draftRow.setMessageType(value);
      case "F" -> draftRow.setConnectionMethod(value);
      case "G" -> draftRow.setStatus(value);
    }
  }
}
