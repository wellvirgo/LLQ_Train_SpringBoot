package vn.dangthehao.train.service.export;

import vn.dangthehao.train.dto.component.DraftImportRow;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;

import java.nio.file.Path;
import java.util.List;

public interface ExportExcelService {
  void exportComponentsAsync(Long jobId, SearchPmhComponentRequest request);

  Path exportFailedImport(List<DraftImportRow> failedRows);
}
