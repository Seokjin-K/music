package com.music.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.music.constatns.FileType;
import com.music.adaptor.FileStorage;
import com.music.dto.upload.LyricsUpload;
import com.music.dto.upload.MusicUpload;
import com.music.dto.upload.FileKeys;
import com.music.dto.upload.TrackUpload;
import com.music.s3.util.S3StorageUtils;
import com.music.s3.validator.S3StorageValidator;
import com.music.util.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
  private final S3StorageValidator s3StorageValidator;

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Override
  public FileKeys uploadTrack(TrackUpload trackUpload) {
    s3StorageValidator.validateTrackUpload(trackUpload);
    return uploadFiles(trackUpload);
  }

  @Override
  public FileKeys uploadMusic(MusicUpload musicUpload) {
    s3StorageValidator.validateMusicUpload(musicUpload);
    return uploadMusicFiles(musicUpload);
  }

  @Override
  public String uploadLyrics(LyricsUpload lyricsUpload) {
    s3StorageValidator.validateLyricsUpload(lyricsUpload);
    File file = lyricsUpload.getLyricsFile();
    ObjectMetadata objectMetadata = S3StorageUtils.createObjectMetadata(
        file.length(),
        lyricsUpload.getContentType(),
        lyricsUpload.getOriginalFilename(),
        lyricsUpload.getDirectory()
    );
    return uploadFile(file, objectMetadata);
  }

  @Override
  public InputStream getFileStream(String musicFileKey) {
    try {
      S3Object s3Object = amazonS3.getObject(bucket, musicFileKey);
      Map<String, String> userMetadata = s3Object.getObjectMetadata().getUserMetadata();

      log.info("musicFileKey : {}", musicFileKey);
      log.info("Stream-Quality : {}", userMetadata.get("quality"));
      return s3Object.getObjectContent();

    } catch (AmazonS3Exception e) {
      log.error("파일 가져오기 실패. key: {}, error: {}", musicFileKey, e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  @Override
  public void deleteFiles(FileKeys fileKeys) {
    try {
      String[] existFileKeys = fileKeys.getExistFileKeys();
      log.info("다중 삭제할 파일 : {}", Arrays.toString(existFileKeys));
      DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket)
          .withKeys(existFileKeys);
      amazonS3.deleteObjects(deleteObjectsRequest);
      log.info("다중 파일 삭제 성공 : {}", Arrays.toString(existFileKeys));
    } catch (AmazonServiceException e) {
      log.error("다중 파일 삭제 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  @Override
  public void deleteFile(String fileKey) {
    try {
      log.info("삭제할 단일 파일 : {}", fileKey);
      amazonS3.deleteObject(bucket, fileKey);
      log.info("단일 파일 삭제 성공 : {}", fileKey);
    } catch (AmazonServiceException e) {
      log.error("단일 파일 삭제 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  private String uploadFile(File file, ObjectMetadata objectMetadata) {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String fileKey = FileType.TRACK.getDirectory() + "/ " + file.getName();
      amazonS3.putObject(new PutObjectRequest(
          bucket,
          fileKey,
          inputStream,
          objectMetadata
      ));
      log.error("파일 업로드 성공 {}", fileKey);
      return fileKey;
    } catch (IOException e) {
      log.error("파일 업로드 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  private FileKeys uploadFiles(TrackUpload trackUpload) {
    MusicUpload musicUpload = trackUpload.getMusicUpload();
    LyricsUpload lyricsUpload = trackUpload.getLyricsUpload();

    String directory = FileType.TRACK.getDirectory();
    List<File> files = trackUpload.getFiles();

    try {
      MultipleFileUpload multipleFileUpload = uploadFilesAtAWSS3(
          musicUpload,
          lyricsUpload,
          directory,
          files
      );
      multipleFileUpload.waitForCompletion();

      log.info("모든 파일 업로드 완료 at uploadFiles");
      return S3StorageUtils.createFileKeys(trackUpload.getFiles(), directory);

    } catch (AmazonServiceException e) {
      log.error("AWS S3 Service error : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } catch (AmazonClientException e) {
      log.error("AWS S3 Client error at uploadMultipleFiles : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("File upload interrupted : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } catch (IOException e) {
      log.error("File upload IOException : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } finally {
      S3StorageUtils.cleanup(files);
    }
  }

  private FileKeys uploadMusicFiles(MusicUpload musicUpload) {
    List<File> files = musicUpload.getFiles();
    String directory = FileType.TRACK.getDirectory();

    try { // AWS 권장 방식 업로드
      MultipleFileUpload multipleFileUpload = uploadFilesAtAWSS3(
          musicUpload,
          null,
          directory,
          files
      );
      multipleFileUpload.waitForCompletion();

      log.info("모든 음원 파일 업로드 완료 at uploadMusicFiles");
      return S3StorageUtils.createFileKeys(musicUpload.getFiles(), directory);

    } catch (AmazonServiceException e) {
      log.error("Music upload AmazonServiceException : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } catch (AmazonClientException e) {
      log.error("AWS S3 Client error at uploadMusicFiles : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경

    } catch (InterruptedException e) {
      log.error("Music upload InterruptedException : {}", e.getMessage());
      throw new RuntimeException(e);

    } catch (IOException e) {
      log.error("Music upload IOException : {}", e.getMessage());
      throw new RuntimeException(e);

    } finally {
      S3StorageUtils.cleanup(files);
    }
  }

  private MultipleFileUpload uploadFilesAtAWSS3(
      MusicUpload musicUpload,
      LyricsUpload lyricsUpload,
      String directory,
      List<File> files
  ) throws IOException {
    // AWS 권장 방식 업로드
    ObjectMetadataProvider provider = new CustomObjectMetadataProvider(musicUpload, lyricsUpload);
    return transferManager.uploadFileList(
        bucket,
        directory,
        FileUtils.createTempFile(), // 업로드 대상 파일이 공통된 기본 디렉토리 하위에 있어야 함
        files,
        provider
    );
  }
}
