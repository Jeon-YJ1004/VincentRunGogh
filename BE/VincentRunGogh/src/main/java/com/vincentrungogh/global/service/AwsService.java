package com.vincentrungogh.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AwsService {

    @Value("${cloud.aws.s3.bucket}")
    private String name;

    private final AmazonS3 s3Client;

    // UUID를 통해 파일명을 바꾸고 S3에 업로드
    public String uploadFile(String file) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(file.getBytes(StandardCharsets.UTF_8));

            String fileName = "route/" + UUID.randomUUID()+".jpg";

            // S3에 업로드할 InputStream 생성
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);

            // S3에 파일 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(decodedBytes.length);
            s3Client.putObject(new PutObjectRequest(name, fileName, inputStream, metadata));

            inputStream.close(); // InputStream 닫기

            return fileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.FAILED_CONVERT_FILE);
        }
    }

    public String getImageUrl(String userUrl) {
        URL url = s3Client.getUrl(name, userUrl);
        return "" + url;
    }

    public void deleteImage(String imgUrl) {
        String key = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);


        s3Client.deleteObject(new DeleteObjectRequest(name, key));
    }
}

