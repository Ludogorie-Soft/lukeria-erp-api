//package com.example.ludogoriesoft.lukeriaerpapi.services;
//
//import io.minio.GetObjectArgs;
//import io.minio.GetObjectResponse;
//import io.minio.MinioClient;
//import io.minio.PutObjectArgs;
//import io.minio.RemoveObjectArgs;
//import io.minio.errors.MinioException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ImageServiceDigitalOcean {
//
//    private final MinioClient minioClient;
//
//    @Value("${digital.ocean.bucket.name}")
//    private String digitalOceanBucketName;
//
//    public String uploadImage(final MultipartFile file, String randomUuid) {
//        try {
//            minioClient.putObject(PutObjectArgs.builder()
//                    .bucket(digitalOceanBucketName)
//                    .object(randomUuid)
//                    .contentType(file.getContentType())
//                    .stream(file.getInputStream(), file.getSize(), -1)
//                    .build());
//            return randomUuid;
//        } catch (MinioException e) {
//            log.warn("Minio error: " + e.getMessage());
//        } catch (IOException e) {
//            log.warn("Error uploading file: " + e.getMessage());
//        } catch (Exception e) {
//            log.warn("An unexpected error occurred: " + e.getMessage());
//        }
//        return null;
//    }
//
//    public byte[] getImageByName(String imageName) {
//        try {
//            GetObjectResponse objectResponse = minioClient.getObject(GetObjectArgs.builder()
//                    .bucket(digitalOceanBucketName)
//                    .object(imageName)
//                    .build());
//            try (InputStream inputStream = objectResponse) {
//                byte[] imageBytes = IOUtils.toByteArray(inputStream);
//                return imageBytes;
//            }
//        } catch (MinioException e) {
//            log.warn("Minio error: " + e.getMessage());
//        } catch (Exception e) {
//            log.warn("An unexpected error occurred: " + e.getMessage());
//        }
//        return null;
//    }
//
//    public void deleteImage(String objectName) {
//        try {
//            minioClient.removeObject(RemoveObjectArgs.builder()
//                    .bucket(digitalOceanBucketName)
//                    .object(objectName)
//                    .build());
//            log.info("Image deleted successfully: " + objectName);
//        } catch (MinioException e) {
//            log.warn("Minio error: " + e.getMessage());
//        } catch (Exception e) {
//            log.warn("An unexpected error occurred: " + e.getMessage());
//        }
//    }
//
//}
