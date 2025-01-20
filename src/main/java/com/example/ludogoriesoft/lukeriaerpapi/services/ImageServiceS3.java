package com.example.ludogoriesoft.lukeriaerpapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class ImageServiceS3 {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private final String bucketName;

    public ImageServiceS3(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }


    public String uploadImage(MultipartFile file, String imageName) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(imageName)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            return imageName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }


    public byte[] getImageByName(String imageName) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(imageName)
                            .build(),
                    ResponseTransformer.toBytes()).asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve image", e);
        }
    }


    public void deleteImage(String imageName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build());
    }

}
