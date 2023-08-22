package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${image.upload.directory}")
    private String imageDirectory;
    private final PackageRepository packageRepository;

    public ImageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public String saveFileAndGetUniqueFilename(MultipartFile file) throws IOException {
        String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
        Path filePath = createFilePath(uniqueFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    String generateUniqueFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    Path createFilePath(String uniqueFilename) throws IOException {
        Path directoryPath = Paths.get(imageDirectory);
        Files.createDirectories(directoryPath);
        return directoryPath.resolve(uniqueFilename);
    }

    public String saveImageForPackage(MultipartFile file) throws IOException {
        String uniqueFilename = saveFileAndGetUniqueFilename(file);

        Package aPackage = packageRepository.findFirstByDeletedFalseOrderByIdDesc();
        aPackage.setPhoto(uniqueFilename);
        packageRepository.save(aPackage);

        return uniqueFilename;
    }

    public String editImageForPackage(MultipartFile file, Long packageId) throws IOException {
        String uniqueFilename = saveFileAndGetUniqueFilename(file);

        Optional<Package> aPackage = packageRepository.findByIdAndDeletedFalse(packageId);
        aPackage.ifPresent(pkg -> {
            pkg.setPhoto(uniqueFilename);
            packageRepository.save(pkg);
        });

        return uniqueFilename;
    }

    public byte[] getImageBytes(String imageName) throws IOException {
        try {
            Path imagePath = Paths.get(imageDirectory, imageName);
            return Files.readAllBytes(imagePath);
        } catch (NoSuchFileException e) {
            return new byte[0];
        }
    }

}

