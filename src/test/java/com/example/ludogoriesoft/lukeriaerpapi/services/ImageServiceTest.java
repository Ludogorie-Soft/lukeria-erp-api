package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

class ImageServiceTest {

    private ImageService imageService;

    @Mock
    private PackageRepository packageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String imageDirectory = "src/main/resources/static/PackageImages/";
        imageService = new ImageService(imageDirectory, packageRepository);
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
    void testSaveImageForPackageThrowsNullPointerException() throws IOException {
        when(packageRepository.findFirstByDeletedFalseOrderByIdDesc()).thenReturn(null); // Връщаме null вместо aPackage

        MockMultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "Test image content".getBytes()
        );

        assertThrows(NullPointerException.class, () -> {
            imageService.saveImageForPackage(file);
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
    void testGenerateUniqueFilename() {
        String originalFilename = "example.jpg";
        String uniqueFilename = imageService.generateUniqueFilename(originalFilename);
        Assertions.assertTrue(uniqueFilename.matches("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}_" + originalFilename + "$"));
    }

    @Test
    void testGetImageBytes() throws IOException {
        String imageName = "test-image.jpg";
        String imageDirectory="src/main/resources/static/PackageImages/";
        Path imagePath = Path.of(imageDirectory, imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, "Test image content".getBytes());

        byte[] imageBytes = imageService.getImageBytes(imageName);

        Assertions.assertArrayEquals("Test image content".getBytes(), imageBytes);
    }

    @Test
    void testGetImageBytesNonExistentImage() throws IOException {
        String nonExistentImageName = "non-existent-image.jpg"; // Подразбира се, че това изображение не съществува

        byte[] imageBytes = imageService.getImageBytes(nonExistentImageName);

        Assertions.assertArrayEquals(new byte[0], imageBytes);
    }
}

