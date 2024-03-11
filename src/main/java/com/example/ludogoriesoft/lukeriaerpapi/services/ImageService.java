package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.PackageNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.PlateNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.Image;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ImageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImageService {
    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final ImageRepository imageRepository;
    private final ImageServiceDigitalOcean imageServiceDigitalOcean;


    public ImageService(PackageRepository packageRepository, PlateRepository plateRepository, ImageRepository imageRepository, ImageServiceDigitalOcean imageServiceDigitalOcean) {
        this.packageRepository = packageRepository;
        this.plateRepository = plateRepository;
        this.imageRepository = imageRepository;
        this.imageServiceDigitalOcean = imageServiceDigitalOcean;
    }

    public String saveImageForPackage(MultipartFile file) {
        String imageName = createImageForSave(packageRepository.findFirstByDeletedFalseOrderByIdDesc(), null);
        return imageServiceDigitalOcean.uploadImage(file, imageName);
    }

    public String editImageForPackage(MultipartFile file, Long packageId) {
        Package aPackage = packageRepository.findByIdAndDeletedFalse(packageId).orElseThrow(() -> new PackageNotFoundException(packageId));
        aPackage.setPhoto(createImageForSave(aPackage, null));
        imageServiceDigitalOcean.uploadImage(file, aPackage.getPhoto());
        return aPackage.getPhoto();
    }

    public String saveImageForPlate(MultipartFile file) {
        String imageName = createImageForSave(null, plateRepository.findFirstByDeletedFalseOrderByIdDesc());
        return imageServiceDigitalOcean.uploadImage(file, imageName);
    }

    public String editImageForPlate(MultipartFile file, Long plateId) {
        Plate plate = plateRepository.findByIdAndDeletedFalse(plateId).orElseThrow(() -> new PlateNotFoundException(plateId));
        plate.setPhoto(createImageForSave(null, plate));
        imageServiceDigitalOcean.uploadImage(file, plate.getPhoto());
        return plate.getPhoto();
    }

    public byte[] getImageBytes(String imageName) {
        return (imageServiceDigitalOcean.getImageByName(imageName));
    }

    public void deleteImageFromSpace(String imageName) {
        imageServiceDigitalOcean.deleteImage(imageName);
    }

    private String createImageForSave(Package pac, Plate plate) {
        UUID fileName = UUID.randomUUID();
        Image image = new Image();
        image.setName(fileName);
        imageRepository.save(image);
        if (pac != null) {
            pac.setPhoto(fileName.toString());
            packageRepository.save(pac);
        } else if (plate != null) {
            plate.setPhoto(fileName.toString());
            plateRepository.save(plate);
        }
        return image.getName().toString();
    }

}

