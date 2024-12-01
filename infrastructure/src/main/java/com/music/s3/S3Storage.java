package com.music.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.music.constatns.AudioQuality;
import com.music.constatns.FileType;
import com.music.adaptor.FileStorage;
import com.music.upload.dto.LyricsUpload;
import com.music.upload.dto.MusicUpload;
import com.music.upload.dto.FileKeys;
import com.music.upload.dto.TrackUpload;
import com.music.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Storage implements FileStorage {

  private final AmazonS3 amazonS3;
  private final TransferManager transferManager;
  private final FileValidator fileValidator;

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Override
  public FileKeys uploadTrack(TrackUpload trackUpload) {
    validateTrackUpload(trackUpload);
    return uploadMultipleFiles(trackUpload);
  }

  @Override
  public InputStream getFileStream(String fileKey) {
    try {
      return amazonS3.getObject(bucket, fileKey).getObjectContent();
    } catch (AmazonS3Exception e) {
      log.error("파일 가져오기 실패. key: {}, error: {}", fileKey, e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }

  private void validateTrackUpload(TrackUpload trackUpload) {
    MusicUpload musicUpload = trackUpload.getMusicUpload();
    trackUpload.getLyricsUploadOptional()
        .ifPresent(lyrics -> {
          fileValidator.validateFileWithMetadata(
              lyrics.getLyricsFile(),
              lyrics.getContentType(),
              lyrics.getOriginalFilename()
          );
        });

    for (Map.Entry<AudioQuality, File> entry : musicUpload.getFileByAudioQuality().entrySet()) {
      fileValidator.validateFileExists(entry.getValue());
    }
    fileValidator.validateContentType(musicUpload.getContentType());
    fileValidator.validateFileName(musicUpload.getOriginalFilename());
  }

  private FileKeys uploadMultipleFiles(TrackUpload trackUpload) {
    MusicUpload musicUpload = trackUpload.getMusicUpload();
    LyricsUpload lyricsUpload = trackUpload.getLyricsUpload();

    List<File> files = trackUpload.getFiles();
    String directory = FileType.TRACK.getDirectory();

    ObjectMetadataProvider provider = new CustomObjectMetadataProvider(musicUpload, lyricsUpload);
    try { // AWS 권장 방식 업로드
      MultipleFileUpload multipleFileUpload = transferManager.uploadFileList(
          bucket,
          directory,
          FileUtils.createTempFile(), // 업로드 대상 파일이 공통된 기본 디렉토리 하위에 있어야 함
          files,
          provider
      );

      multipleFileUpload.addProgressListener(createProgressListener());
      multipleFileUpload.waitForCompletion();

      log.info("모든 파일 업로드 완료");
      return createFileKeys(trackUpload, directory);
    } catch (AmazonServiceException e) {
      log.error("AWS S3 Service error : {}", e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    } catch (AmazonClientException e) {
      log.error("AWS S3 Client error : {}", e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("File upload interrupted : {}", e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    } finally {
      cleanup(files);
    }
  }

  private ProgressListener createProgressListener() {
    return progressEvent -> {
      log.info("EventType : {}", progressEvent.getEventType());
      double pct = progressEvent.getBytesTransferred() * 100.0 / progressEvent.getBytes();
      log.info("전체 파일 업로드 진행률: {}%", pct);
    };
  }

  private FileKeys createFileKeys(TrackUpload trackUpload, String directory) {
    List<File> files = trackUpload.getFiles();
    List<String> fileKeyList = new ArrayList<>(files.size());
    directory += "/";

    for (File file : files){
      fileKeyList.add(directory + file.getName());
    }

    return FileKeys.builder()
        .highQualityKey(fileKeyList.get(0))
        .mediumQualityKey(fileKeyList.get(1))
        .lowQualityKey(fileKeyList.get(2))
        .lyricsKey(fileKeyList.size() > 3 ? fileKeyList.get(3) : null)
        .build();
  }

  private void cleanup(List<File> files) {
    //transferManager.shutdownNow(false); // transferManager 종료
    for (File file : files) {
      if (!file.delete()) {
        log.warn("임시 파일 삭제 실패 : {}", file.getAbsolutePath());
      }
    }
  }
}
