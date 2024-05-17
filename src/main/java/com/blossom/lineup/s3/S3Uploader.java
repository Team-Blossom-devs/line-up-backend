package com.blossom.lineup.s3;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.base.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    public String saveFile(MultipartFile file, String folderName){
        // ex - organization/uuid.png
        String randomFilePath = generateUniqueFilename(file,folderName);

        log.info("File upload started : " + randomFilePath);

        // 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try{
            // s3에 파일 업로드
            amazonS3.putObject(bucket, randomFilePath, file.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            // S3 예외 처리
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new ServerException(Code.S3_UPLOAD_FAIL);  // 업로드 실패 예외 발생
        } catch (SdkClientException e) {
            // SDK 클라이언트 예외 처리
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new ServerException(Code.S3_UPLOAD_FAIL);  // 업로드 실패 예외 발생
        } catch (IOException e) {
            // IO 예외 처리
            log.error("IO error while uploading file: " + e.getMessage());
            throw new ServerException(Code.S3_UPLOAD_FAIL);  // 업로드 실패 예외 발생
        }

        log.info("File upload completed : " + randomFilePath);

        // 업로드된 파일의 url 반환
        return amazonS3.getUrl(bucket, randomFilePath).toString();
    }

    private String generateUniqueFilename(MultipartFile file, String folderName){
        String originalFilename = file.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilePath;

        do{
            // ex - organization/uuid.png
            randomFilePath = folderName+ "/"+ UUID.randomUUID() + "." + fileExtension;
        } while(amazonS3.doesObjectExist(bucket, randomFilePath)); // 파일명이 중복될 경우 새로운 UUID 생성

        return randomFilePath;
    }
    private String validateFileExtension(String originalFilename){
        // 파일 확장자
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")+1).toLowerCase();

        // 허용된 확장자 목록
        List<String> allowedExtensions = Arrays.asList("jpg","png","gif","jpeg");

        // 허용되지 않은 확장자일 경우에 예외 발생
        if(!allowedExtensions.contains(fileExtension)){
            throw new BusinessException(Code.NOT_IMAGE_EXTENSION);
        }

        return fileExtension;
    }
}
