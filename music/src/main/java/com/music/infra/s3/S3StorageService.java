package com.music.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.music.constatns.FileType;
import com.music.service.FileStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary // 구현체가 여러 개일 때 해당 클래스를 우선 주입
@RequiredArgsConstructor
@Slf4j
public class S3StorageService implements FileStorageService {

  private final AmazonS3 amazonS3;

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Override
  public String uploadFile(MultipartFile file, FileType fileType) {
    validateFile(file, fileType); // 검증
    return s3UploadFile(file, fileType); // 업로드 성공 시 파일키 반환
  }

  private String s3UploadFile(MultipartFile file, FileType fileType) {
    String directory = fileType.getDirectory();
    String fileName = createFileName(file.getOriginalFilename(), directory);
    ObjectMetadata metadata = createMetadata(file);

    // InputStream 방식
    // TODO: 추후에 더 효율적인 방식으로 변경
    try (InputStream inputStream = file.getInputStream()) {

      PutObjectRequest putObjectRequest =
          new PutObjectRequest(bucket, fileName, inputStream, metadata);
      amazonS3.putObject(putObjectRequest);

      String fileKey = amazonS3.getUrl(bucket, fileName).toString();
      log.info("파일 업로드 성공. fileKey: {}, fileName: {}, metadata: {}",
          fileKey, fileName, metadata);
      return fileKey;

    } catch (IOException e) {
      log.error("파일 업로드 실패. fileType: {}, fileName: {}", directory, fileName);
      throw new RuntimeException(); // TODO: CustomException 으로 변경

    } catch (AmazonS3Exception e) {
      log.error("S3 업로드 중 오류 발생. fileType: {}, fileName: {}, errorCode: {}",
          directory, fileName, e.getErrorCode(), e);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }

  private String createFileName(String originalFilename, String directory) {
    return String.format("%s/%s_%s", directory, UUID.randomUUID(), originalFilename);
  }

  private ObjectMetadata createMetadata(MultipartFile file) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());
    return metadata;
  }

  private void validateFile(MultipartFile file, FileType fileType) {
    validateBasicFile(file);
    fileType.getFileValidator().validate(file); // 타입에 따라 다른 구현체의 validate 메서드 실행
  }

  private void validateBasicFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.error("파일이 비어있습니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    String fileName = file.getOriginalFilename();
    if (fileName == null || fileName.trim().isEmpty()) {
      log.error("파일명이 유효하지 않습니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    String contentType = file.getContentType();
    if (contentType == null || contentType.trim().isEmpty()) {
      log.error("파일 형식을 확인할 수 없습니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
