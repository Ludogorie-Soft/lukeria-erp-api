package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.ImageEntity;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${image.upload.directory}")
    private String imageUploadDirectory;

    @Autowired
    private ImageRepository imageRepository;

    public String saveImage(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path directoryPath = Paths.get(imageUploadDirectory);
            Path filePath = directoryPath.resolve(filename);

            Files.createDirectories(directoryPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setPath(filePath.toString());
            imageRepository.save(imageEntity);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image: " + e.getMessage());
        }
    }

}

