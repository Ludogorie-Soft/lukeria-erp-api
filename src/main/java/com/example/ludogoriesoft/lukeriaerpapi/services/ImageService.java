package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ImageProcessingException;
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


    public String saveImageForPackage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            Path directoryPath = Paths.get(imageDirectory);
            Path filePath = directoryPath.resolve(uniqueFilename);

            Files.createDirectories(directoryPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Package aPackage = packageRepository.findFirstByDeletedFalseOrderByIdDesc();
            aPackage.setPhoto(uniqueFilename);
            packageRepository.save(aPackage);

            return uniqueFilename;
        } catch (IOException e) {
            throw new ImageProcessingException("Error saving image: " + e.getMessage(), e);
        }
    }

    public String editImageForPackage(MultipartFile file, Long packageId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            Path directoryPath = Paths.get(imageDirectory);
            Path filePath = directoryPath.resolve(uniqueFilename);

            Files.createDirectories(directoryPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Optional<Package> aPackage = packageRepository.findByIdAndDeletedFalse(packageId);
            aPackage.get().setPhoto(uniqueFilename);
            packageRepository.save(aPackage.get());

            return uniqueFilename;
        } catch (IOException e) {
            throw new ImageProcessingException("Error saving image: " + e.getMessage(), e);
        }
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

