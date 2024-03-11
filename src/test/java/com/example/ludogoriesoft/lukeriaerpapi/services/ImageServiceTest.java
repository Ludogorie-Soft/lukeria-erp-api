package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ImageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

class ImageServiceTest {

    private ImageService imageService;

    @Mock
    private PackageRepository packageRepository;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ImageServiceDigitalOcean imageServiceDigitalOcean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String imageDirectory = "src/main/resources/static/uploads/";
        imageService = new ImageService( packageRepository, plateRepository, imageRepository, imageServiceDigitalOcean);
    }

    @Test
    void testEditImageForPackage() throws IOException {
        Long packageId = 1L;
        Package aPackage = new Package();
        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(Optional.of(aPackage));

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        String uniqueFilename = imageService.editImageForPackage(file, packageId);

        Assertions.assertNotNull(aPackage); // Проверка за null стойност
        Assertions.assertEquals(uniqueFilename, aPackage.getPhoto());
    }

    @Test
    void testEditImageForPackageThrowsNullPointerException() throws IOException {
        Long packageId = 1L;
        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(null); // Връщаме null вместо Optional.of(aPackage)

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        assertThrows(NullPointerException.class, () -> {
            imageService.editImageForPackage(file, packageId);
        });
    }


    @Test
    void testEditImageForPackageThrowsNullPointerException2() throws IOException {
        Long packageId = 1L;
        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(null); // Връщаме null вместо Optional.of(aPackage)

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        assertThrows(NullPointerException.class, () -> {
            imageService.editImageForPackage(file, packageId);
        });
    }





    @Test
    void testGetImageBytesNonExistentImage() throws IOException {
        String nonExistentImageName = "non-existent-image.jpg"; // Подразбира се, че това изображение не съществува

        byte[] imageBytes = imageService.getImageBytes(nonExistentImageName);

        assertNull(imageBytes);
    }

    @Test
    void testEditImageForPlate() throws IOException {
        Long plateId = 1L;
        Plate aPlate = new Plate();
        when(plateRepository.findByIdAndDeletedFalse(plateId)).thenReturn(Optional.of(aPlate));

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        String uniqueFilename = imageService.editImageForPlate(file, plateId);

        Assertions.assertNotNull(aPlate); // Проверка за null стойност
        Assertions.assertEquals(uniqueFilename, aPlate.getPhoto());
    }

    @Test
    void testEditImageForPlateThrowsNullPointerException() throws IOException {
        Long plateId = 1L;
        when(plateRepository.findByIdAndDeletedFalse(plateId)).thenReturn(null); // Връщаме null вместо Optional.of(aPlate)

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        assertThrows(NullPointerException.class, () -> {
            imageService.editImageForPlate(file, plateId);
        });
    }


    @Test
    void testEditImageForPlateThrowsNullPointerException2() throws IOException {
        Long plateId = 1L;
        when(plateRepository.findByIdAndDeletedFalse(plateId)).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        assertThrows(NullPointerException.class, () -> {
            imageService.editImageForPlate(file, plateId);
        });
    }


}

