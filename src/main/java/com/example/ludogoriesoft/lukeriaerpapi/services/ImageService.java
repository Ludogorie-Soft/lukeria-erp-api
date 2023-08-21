package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
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
    private String IMAGE_DIRECTORY;
    private final PackageRepository packageRepository;


    public ImageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }


    public String saveImageForPackage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            Path directoryPath = Paths.get(IMAGE_DIRECTORY);
            Path filePath = directoryPath.resolve(uniqueFilename);

            Files.createDirectories(directoryPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Package aPackage = packageRepository.findFirstByDeletedFalseOrderByIdDesc();
            aPackage.setPhoto(uniqueFilename);
            packageRepository.save(aPackage);

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image: " + e.getMessage());
        }
    }

    public byte[] getImageBytes(String imageName) throws IOException {
        Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
        return Files.readAllBytes(imagePath);
    }
}

