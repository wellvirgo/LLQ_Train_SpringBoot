package vn.dangthehao.train.service.export;

import jakarta.servlet.http.HttpServletResponse;
import vn.dangthehao.train.dto.component.FailedImportRow;
import vn.dangthehao.train.entity.PmhComponents1;

import java.nio.file.Path;
import java.util.List;

public interface ExportExcelService {
  void exportComponents(HttpServletResponse response, List<PmhComponents1> pmhComponents1);

  Path exportFailedImport(List<FailedImportRow> failedRows);
}
