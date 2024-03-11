package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.PackageNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.PlateNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.Image;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ImageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {
    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final ImageRepository imageRepository;
    private final ImageServiceDigitalOcean imageServiceDigitalOcean;

    public String saveImageForPackage(MultipartFile file) {
        String imageName = createImageForSave(packageRepository.findFirstByDeletedFalseOrderByIdDesc());
        return imageServiceDigitalOcean.uploadImage(file, imageName);
    }

    public String editImageForPackage(MultipartFile file, Long packageId) {
        if (!file.isEmpty()) {
            Package aPackage = packageRepository.findByIdAndDeletedFalse(packageId).orElseThrow(() -> new PackageNotFoundException(packageId));
            aPackage.setPhoto(createImageForSave(aPackage));
            imageServiceDigitalOcean.uploadImage(file, aPackage.getPhoto());
            return aPackage.getPhoto();
        }
        return null;
    }

    public String saveImageForPlate(MultipartFile file) {
        String imageName = createImageForSave(plateRepository.findFirstByDeletedFalseOrderByIdDesc());
        return imageServiceDigitalOcean.uploadImage(file, imageName);
    }

    public String editImageForPlate(MultipartFile file, Long plateId) {
        if (!file.isEmpty()) {
            Plate plate = plateRepository.findByIdAndDeletedFalse(plateId).orElseThrow(() -> new PlateNotFoundException(plateId));
            plate.setPhoto(createImageForSave(plate));
            imageServiceDigitalOcean.uploadImage(file, plate.getPhoto());
            return plate.getPhoto();
        }
        return null;
    }

    public byte[] getImageBytes(String imageName) {
        return (imageServiceDigitalOcean.getImageByName(imageName));
    }

    public void deleteImageFromSpace(String imageName) {
        imageRepository.delete(imageRepository.findByName(UUID.fromString(imageName)));
        imageServiceDigitalOcean.deleteImage(imageName);
    }

    private String createImageForSave(Object entity) {
        UUID fileName = UUID.randomUUID();
        Image image = new Image();
        image.setName(fileName);
        if (entity instanceof Package pack) {
            pack.setPhoto(fileName.toString());
            packageRepository.save(pack);
            image.setPackageImage(pack);
        } else if (entity instanceof Plate plate) {
            plate.setPhoto(fileName.toString());
            plateRepository.save(plate);
            image.setPlateImage(plate);
        }
        imageRepository.save(image);
        return image.getName().toString();
    }

}

