package com.example.ludogoriesoft.lukeriaerpapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class UploadFromFileServiceTest {

  @Mock private PackageRepository packageRepository;

  @Mock private ProductRepository productRepository;

  @Mock private PlateService plateService;

  @Mock private CartonService cartonService;

  @InjectMocks private UploadFromFileService uploadFromFileService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void whenRepositoryIsEmpty_thenReturnsConflictWithMessage() throws IOException {
    when(packageRepository.findAll()).thenReturn(Collections.emptyList());

    ResponseEntity<String> response = uploadFromFileService.packageUploadStatus(null);

    assertEquals("File is null.", response.getBody());
    assertEquals(409, response.getStatusCodeValue());
  }

  @Test
  void whenRepositoryIsEmpty_thenReturnsOkWithMessage() throws IOException {
    when(packageRepository.findAll()).thenReturn(List.of(new Package()));

    ResponseEntity<String> response = uploadFromFileService.packageUploadStatus(null);

    assertEquals("File is already uploaded!", response.getBody());
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  void whenInvalidFileFormat_thenThrowsIOExceptionWithMessage() {
    when(packageRepository.findAll()).thenReturn(Collections.emptyList());

    assertThrows(
        IOException.class,
        () -> {
          uploadFromFileService.packageUploadStatus(createMockMultipartFile());
        },
        "The file is not a valid .xlsx file.");
  }

  @Test
  void uploadSuccessfulFile() throws IOException {
    when(packageRepository.findAll()).thenReturn(Collections.emptyList());
    ResponseEntity<String> response = uploadFromFileService.packageUploadStatus(getTestFile());

    assertEquals("Successfully added all packages.", response.getBody());
      assertEquals(200, response.getStatusCodeValue());

  }

  private static MultipartFile getTestFile() throws IOException {
    InputStream inputStream =
        UploadFromFileServiceTest.class.getResourceAsStream(
            "/static/uploads/Package Data New.xlsx");

    // Преобразуване на InputStream до MultipartFile
    return new MockMultipartFile(
        "file",
        "Package Data New.xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        inputStream);
  }

  private static MultipartFile createMockMultipartFile() throws IOException {
    String content = "tests";
    Path tempFile = Files.createTempFile("test", ".pdf");
    Files.write(tempFile, content.getBytes());

    return new MockMultipartFile(
        "file",
        tempFile.getFileName().toString(),
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        Files.readAllBytes(tempFile));
  }
}
