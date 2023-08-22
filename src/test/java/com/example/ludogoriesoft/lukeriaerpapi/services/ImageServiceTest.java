package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Path;
import static org.testng.AssertJUnit.*;
class ImageServiceTest {
    private ImageService imageService;

    @Mock
    private PackageRepository packageRepository;
    @Mock
    private Path mockFilePath;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService(packageRepository);
    }

    @Test
    void testGenerateUniqueFilename() {
        String originalFilename = "example.jpg";
        String uniqueFilename = imageService.generateUniqueFilename(originalFilename);
        assertTrue(uniqueFilename.matches("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}_" + originalFilename + "$"));
    }
}

