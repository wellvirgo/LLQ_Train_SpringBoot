package vn.dangthehao.train.service.imports;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import vn.dangthehao.train.dto.common.ImportExcelResponse;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ImportFileStatus;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.service.export.ExportExcelService;
import vn.dangthehao.train.service.pmhComponents1.PmhComponents1Service;
import vn.dangthehao.train.util.FileUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ExcelImportComponentService {
  PmhComponents1Service componentService;
  ExportExcelService exportExcelService;

  @Transactional
  // .xlsx is the ZIP archive contains many small .xml files
  public ImportExcelResponse importComponents(MultipartFile file) {
    // OPCPackage as un-zipper and contain .xml files
    try (OPCPackage opcPackage = OPCPackage.open(file.getInputStream())) {
      // Table's used for storing common string
      ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opcPackage);
      // XSSFReader like "Map" to navigate .xml file in OPCPackage
      XSSFReader xssfReader = new XSSFReader(opcPackage);
      // Combine raw value and the rule from StyleTable to produce formatted value
      StylesTable stylesTable = xssfReader.getStylesTable();
      DataFormatter formatter = new DataFormatter();

      Consumer<List<PmhComponents1>> saveCallback = componentService::batchCreateComponents;

      XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
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
        componentService.batchCreateComponents(sheetHandler.getComponents());

        if (!sheetHandler.getErrors().isEmpty()) {
          Path report = exportExcelService.exportFailedImport(sheetHandler.getErrors());
          String reportName = report.getFileName().toString();
          return buildImportResponse(
              ImportFileStatus.COMPLETE_WITH_ERROR, reportName, sheetHandler);
        }

        return buildImportResponse(ImportFileStatus.COMPLETE, null, sheetHandler);
      }

    } catch (IOException | SAXException | OpenXML4JException | ParserConfigurationException e) {
      log.info("Error when reading excel file", e);
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

    return ImportExcelResponse.builder().status(ImportFileStatus.ERROR).build();
  }

  public Resource loadErrorImportReport(String fileName) {
    try {
      Path file = FileUtils.getTempFile(fileName, "excel");
      return new UrlResource(file.toUri());
    } catch (MalformedURLException e) {
      throw new AppException(ErrorCode.UNABLE_READ_FILE, "Invalid path");
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void clearTempErrorReport() {
    FileUtils.deleteTempFiles(".xlsx", "excel");
  }

  private ImportExcelResponse buildImportResponse(
      ImportFileStatus status, String errorReportName, SheetHandler sheetHandler) {
    long success = sheetHandler.getComponents().size();
    long failed = sheetHandler.getErrors().size();
    long total = success + failed;

    return ImportExcelResponse.builder()
        .total(total)
        .success(success)
        .failed(failed)
        .status(status)
        .errorReportName(errorReportName)
        .build();
  }
}
