package com.music.s3.util;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.music.dto.upload.FileKeys;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3StorageUtils {

  public static FileKeys createFileKeys(List<File> files, String directory) {
    List<String> fileKeys = generateFileKeys(files, directory);
    return buildFileKeys(fileKeys);
  }

  public static List<String> generateFileKeys(List<File> files, String directory) {
    List<String> fileKeys = new ArrayList<>(files.size());
    directory += "/";

    for (File file : files) {
      fileKeys.add(directory + file.getName());
    }
    return fileKeys;
  }

  public static FileKeys buildFileKeys(List<String> fileKeyList) {
    return FileKeys.builder()
        .highQualityKey(fileKeyList.get(0))
        .mediumQualityKey(fileKeyList.get(1))
        .lowQualityKey(fileKeyList.get(2))
        .lyricsKey(fileKeyList.size() > 3 ? fileKeyList.get(3) : null)
        .build();
  }

  public static ObjectMetadata createObjectMetadata(
      long length,
      String contentType,
      String originalFileName,
      String directory
  ) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(length);
    objectMetadata.setContentType(contentType);
    objectMetadata.addUserMetadata("originalFilename", originalFileName);
    objectMetadata.addUserMetadata("directory", directory);
    return objectMetadata;
  }

  public static void cleanup(List<File> files) {
    //transferManager.shutdownNow(false); // transferManager 종료
    for (File file : files) {
      if (!file.delete()) {
        log.warn("임시 파일 삭제 실패 : {}", file.getAbsolutePath());
      }
    }
  }
}
