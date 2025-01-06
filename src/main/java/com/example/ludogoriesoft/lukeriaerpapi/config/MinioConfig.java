//package com.example.ludogoriesoft.lukeriaerpapi.config;
//
//import io.minio.MinioClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MinioConfig {
//
//    @Value("${digital.ocean.access.key}")
//    private String accessKey;
//
//    @Value("${digital.ocean.secret.key}")
//    private String secretKey;
//
//    @Value("${space.bucket.origin.url}")
//    private String endpoint;
//
//    @Bean
//    public MinioClient minioClient() {
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .region("fra1")
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}
//
