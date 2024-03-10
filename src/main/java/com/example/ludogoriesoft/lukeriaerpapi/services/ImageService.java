package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Image;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ImageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final ImageRepository imageRepository;
    private final ImageServiceDigitalOcean imageServiceDigitalOcean;
    @Value("${image.upload.directory}")
    private final String imageDirectory;


    public ImageService(@Value("${image.upload.directory}") String imageDirectory, PackageRepository packageRepository, PlateRepository plateRepository, ImageRepository imageRepository, ImageServiceDigitalOcean imageServiceDigitalOcean) {
        this.imageDirectory = imageDirectory;
        this.packageRepository = packageRepository;
        this.plateRepository = plateRepository;
        this.imageRepository = imageRepository;
        this.imageServiceDigitalOcean = imageServiceDigitalOcean;
    }

    public String saveFileAndGetUniqueFilename(MultipartFile file) throws IOException {
        String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
        Path filePath = createFilePath(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private String generateUniqueFilename(String originalFilename) {
        return UUID.randomUUID().toString();
    }

    Path createFilePath(String uniqueFilename) throws IOException {
        Path directoryPath = Paths.get(imageDirectory);
        Files.createDirectories(directoryPath);
        return directoryPath.resolve(uniqueFilename);
    }

    public String saveImageForPackage(MultipartFile file) {
        UUID fileName = UUID.randomUUID();
        Image image = new Image();
        image.setName(fileName);
        Package aPackage = packageRepository.findFirstByDeletedFalseOrderByIdDesc();
        image.setPackageImage(aPackage);
        imageRepository.save(image);
        String imageNameInSpace = imageServiceDigitalOcean.uploadImage(file, image.getName().toString());
        return "Name of the Image - " + imageNameInSpace;
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

    public String saveImageForPlate(MultipartFile file) throws IOException {
        String uniqueFilename = saveFileAndGetUniqueFilename(file);
        Plate plate = plateRepository.findFirstByDeletedFalseOrderByIdDesc();
        plate.setPhoto(uniqueFilename);
        plateRepository.save(plate);

        return uniqueFilename;
    }

    public String editImageForPlate(MultipartFile file, Long packageId) throws IOException {
        String uniqueFilename = saveFileAndGetUniqueFilename(file);

        Optional<Plate> plate = plateRepository.findByIdAndDeletedFalse(packageId);
        plate.ifPresent(aPlate -> {
            aPlate.setPhoto(uniqueFilename);
            plateRepository.save(aPlate);
        });
        return uniqueFilename;
    }

}

