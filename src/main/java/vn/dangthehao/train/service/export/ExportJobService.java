package vn.dangthehao.train.service.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vn.dangthehao.train.dto.common.FileStreamResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.AppUser;
import vn.dangthehao.train.entity.ExportJob;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.repository.ExportJobRepository;
import vn.dangthehao.train.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ExportJobService {
  ObjectMapper objectMapper;
  ExportJobRepository exportJobRepository;
  UserRepository userRepository;

  public ExportJob createExportJob(String username, SearchPmhComponentRequest request) {
    AppUser user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));

    String filterPayload;
    try {
      filterPayload = this.objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    ExportJob exportJob = ExportJob.builder().user(user).filterPayload(filterPayload).build();
    return exportJobRepository.save(exportJob);
  }

  public ExportJob getExportJob(Long id) {
    return exportJobRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Export job not found"));
  }

  public String getStatusById(Long id) {
    return exportJobRepository.findStatusById(id);
  }

  public FileStreamResult streamZippedFile(Long id) {
    final Path dirLocationPath = Paths.get("temp/excel").toAbsolutePath().normalize();
    ExportJob exportJob = getExportJob(id);
    String filename = exportJob.getFileName();

    if (!"DONE".equals(exportJob.getStatus())) {
      throw new AppException(ErrorCode.BAD_REQUEST, "The file is not already done processing");
    }

    Path filePath = dirLocationPath.resolve(filename).normalize();
    if (!(Files.exists(filePath) && Files.isReadable(filePath))) {
      throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "The file is not readable");
    }

    return getDownloadingResultMap(filename, filePath);
  }

  private static FileStreamResult getDownloadingResultMap(String filename, Path filePath) {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd_HHmm");
    final String zipFileName = "export_component_" + formatter.format(now) + ".zip";

    StreamingResponseBody stream =
        outputStream -> {
          try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            ZipEntry zipEntry = new ZipEntry(filename);
            zipOutputStream.putNextEntry(zipEntry);
            Files.copy(filePath, zipOutputStream);
            zipOutputStream.closeEntry();
          } catch (IOException e) {
            log.error("Error while zipping file", e);
            throw new AppException(ErrorCode.IO_ERROR);
          }
        };

    return new FileStreamResult(zipFileName, stream);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatus(Long id, String status) {
    ExportJob exportJob = getExportJob(id);
    exportJob.setStatus(status);
    exportJobRepository.saveAndFlush(exportJob);
  }

  public void completeExportJob(Long id, String fileName, String exportUrl) {
    ExportJob exportJob = getExportJob(id);
    exportJob.setStatus("DONE");
    exportJob.setFileName(fileName);
    exportJob.setExportUrl(exportUrl);
    exportJobRepository.save(exportJob);
  }

  public void failExportJob(Long id) {
    ExportJob exportJob = getExportJob(id);
    exportJob.setStatus("FAILED");
    exportJobRepository.save(exportJob);
  }
}
