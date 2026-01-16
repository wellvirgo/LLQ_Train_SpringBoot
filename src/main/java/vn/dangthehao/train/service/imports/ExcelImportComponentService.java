package vn.dangthehao.train.service.imports;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.service.export.ExportExcelService;
import vn.dangthehao.train.service.pmhComponents1.PmhComponents1Service;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ExcelImportComponentService {
  PmhComponents1Service componentService;
  ExportExcelService exportExcelService;

  // .xlsx is the ZIP archive contains many small .xml files
  public void importComponents(HttpServletResponse response, MultipartFile file) {
    // OPCPackage as un-zipper and contain .xml files
    try (OPCPackage opcPackage = OPCPackage.open(file.getInputStream())) {
      // Table's used for storing common string
      ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opcPackage);
      // XSSFReader like "Map" to navigate .xml file in OPCPackage
      XSSFReader xssfReader = new XSSFReader(opcPackage);
      StylesTable stylesTable = xssfReader.getStylesTable();
      // Combine raw value and the rule from StyleTable to produce formatted value
      DataFormatter formatter = new DataFormatter();

      Consumer<List<PmhComponents1>> saveCallback = componentService::batchCreateComponents;

      XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
      while (iterator.hasNext()) {
        try (InputStream inputStream = iterator.next()) {
          SheetHandler sheetHandler = new SheetHandler(saveCallback);
          // Standard Java XML parser
          XMLReader parser = XMLHelper.newXMLReader();

          // The bridge to connect xml-reader, style, shared string table, sheet handler(contain
          // business logic)
          XSSFSheetXMLHandler contentHandler =
              new XSSFSheetXMLHandler(stylesTable, strings, sheetHandler, formatter, false);
          parser.setContentHandler(contentHandler);

          parser.parse(new InputSource(inputStream));
          sheetHandler.processRemaining();
          if (!sheetHandler.getErrors().isEmpty()) {
            exportExcelService.exportFailedImport(response, sheetHandler.getErrors());
          }
        }
      }
    } catch (Exception e) {
      log.info("Error when reading excel file", e);
    }
  }
}
