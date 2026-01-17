package vn.dangthehao.train.util;

import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {
  private static final String TEMP_DIR = "temp";

  public static Path createTempFile(String prefix, String extension, String... parents) {
    Objects.requireNonNull(prefix);
    Objects.requireNonNull(extension);

    if (prefix.isBlank())
      throw new AppException(ErrorCode.UNABLE_CREATE_FILE, "Prefix file name is blank");

    if (extension.isBlank())
      throw new AppException(ErrorCode.UNABLE_CREATE_FILE, "Extension file name is blank");

    Path parent = createDirectories(TEMP_DIR, parents);
    try {
      return Files.createTempFile(parent, prefix + "_", extension);
    } catch (FileAlreadyExistsException e) {
      throw new AppException(ErrorCode.UNABLE_CREATE_FILE, "File already exists");
    } catch (IOException e) {
      throw new AppException(ErrorCode.UNABLE_CREATE_FILE, e.getMessage());
    }
  }

  public static Path getTempFile(String fileName, String... parents) {
    Path parent = Path.of(TEMP_DIR, parents);
    Path filePath = parent.resolve(fileName);
    if (!Files.exists(filePath))
      throw new AppException(ErrorCode.UNABLE_READ_FILE, "File does not exist");
    return filePath;
  }

  @SuppressWarnings("SameParameterValue")
  private static Path createDirectories(String parentDir, String... dirs) {
    Path path;
    try {
      Objects.requireNonNull(parentDir);
      path = Path.of(parentDir, dirs);
      Files.createDirectories(path);
    } catch (NullPointerException | IOException e) {
      throw new AppException(ErrorCode.UNABLE_CREATE_DIRECTORY, e.getMessage());
    }

    return path;
  }
}
