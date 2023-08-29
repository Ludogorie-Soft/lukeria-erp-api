package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

class ImageServiceTest {

    private ImageService imageService;

    @Mock
    private PackageRepository packageRepository;

    private final String imageDirectory = "src/main/resources/static/PackageImages/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService(packageRepository);
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
    void testGetImageBytesThrowsIOException() {
        String imageName = "non-existent-image.jpg"; // Подразбира се, че това изображение не съществува

        assertThrows(NullPointerException.class, () -> {
            imageService.getImageBytes(imageName);
        });
    }

    @Test
    void testGenerateUniqueFilename() {
        String originalFilename = "example.jpg";
        String uniqueFilename = imageService.generateUniqueFilename(originalFilename);
        Assertions.assertTrue(uniqueFilename.matches("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}_" + originalFilename + "$"));
    }
}

