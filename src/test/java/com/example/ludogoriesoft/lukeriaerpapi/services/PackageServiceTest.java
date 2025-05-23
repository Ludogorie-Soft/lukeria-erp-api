package com.example.ludogoriesoft.lukeriaerpapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.EmailContentBuilder;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

class PackageServiceTest {
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private CartonRepository cartonRepository;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductService productService;

    @Mock
    private EmailService emailService;
    @Mock
    private EmailContentBuilder emailContentBuilder;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PackageService packageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPackages() {
        Package package1 = new Package();
        package1.setId(1L);
        package1.setName("Package 1");

        Package package2 = new Package();
        package2.setId(2L);
        package2.setName("Package 2");

        List<Package> mockPackages = Arrays.asList(package1, package2);
        when(packageRepository.findByDeletedFalse()).thenReturn(mockPackages);

        PackageDTO packageDTO1 = new PackageDTO();
        packageDTO1.setId(1L);
        packageDTO1.setName("Package 1");

        PackageDTO packageDTO2 = new PackageDTO();
        packageDTO2.setId(2L);
        packageDTO2.setName("Package 2");

        when(modelMapper.map(package1, PackageDTO.class)).thenReturn(packageDTO1);
        when(modelMapper.map(package2, PackageDTO.class)).thenReturn(packageDTO2);

        List<PackageDTO> result = packageService.getAllPackages();

        assertEquals(mockPackages.size(), result.size());
        assertEquals(mockPackages.get(0).getName(), result.get(0).getName());
        assertEquals(mockPackages.get(1).getName(), result.get(1).getName());

        verify(packageRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockPackages.size())).map(any(Package.class), eq(PackageDTO.class));
    }

    @Test
    void testSendProductStockReportById_PackageNotFound() throws ChangeSetPersister.NotFoundException {
        Long packageId = 1L;

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            packageService.sendProductStockReportById(packageId);
        });

        verify(productService, never()).getProductById(any());
        verify(emailService, never()).sendHtmlEmailWithProductReport(any(), any(), any());
    }

    @Test
    void testCreatePackageWithPlateIdNotNullAndPlateDoesNotExist() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Test Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setEnglishName("name");
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(plateRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }
    @Test
    void testSendProductStockReportById_Success() throws Exception {
        Long packageId = 1L;
        Package productPackage = new Package();
        productPackage.setId(packageId);
        productPackage.setPlateId(new Plate());
        productPackage.setCartonId(new Carton());
        productPackage.setProductCode("code");

        List<String> emails = List.of("email1@example.com", "email2@example.com");
        String emailBody = "Generated Email Body";

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(Optional.of(productPackage));
        when(modelMapper.map(productPackage, PackageDTO.class)).thenReturn(new PackageDTO());
        when(modelMapper.map(any(Plate.class), eq(PlateDTO.class))).thenReturn(new PlateDTO());
        when(modelMapper.map(any(Carton.class), eq(CartonDTO.class))).thenReturn(new CartonDTO());
        when(productService.getProductById(packageId)).thenReturn(new ProductDTO());
        when(emailContentBuilder.generateProductStockReportById(any(), any(), any(), any())).thenReturn(emailBody);
        when(userRepository.findEmailsByRoleNotCustomer()).thenReturn(emails);

        boolean result = packageService.sendProductStockReportById(packageId);

        verify(emailService).sendHtmlEmailWithProductReport(eq(emails), contains("Наличност за Продукт с Код: "), eq(emailBody));
        assertTrue(result);
    }


    @Test
    void testUpdatePackage_ReturnsUpdatedPackageDTO() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);
        packageDTO.setEnglishName("en name");

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(true);
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);
        when(packageRepository.save(existingPackage)).thenReturn(existingPackage);
        when(modelMapper.map(existingPackage, PackageDTO.class)).thenReturn(packageDTO);

        // Act
        PackageDTO updatedPackageDTO = packageService.updatePackage(packageId, packageDTO);

        // Assert
        assertNotNull(updatedPackageDTO);
        assertEquals("Updated Package", updatedPackageDTO.getName());
        // Add more assertions based on your requirements
    }


    @Test
    void testUpdatePackage_ThrowsValidationException_WhenPlateDoesNotExist() {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setEnglishName("name");
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.updatePackage(packageId, packageDTO));
    }

    @Test
    void testUpdatePackage_ThrowsValidationException_WhenPlateNull() {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setEnglishName("name");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(null);

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.updatePackage(packageId, packageDTO));
    }

    @Test
    void testCreatePackage_ThrowsValidationException_WhenPlateDoesNotExist() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("New Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setEnglishName("name");
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testCreatePackageWithPlateIdNull() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Test Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setEnglishName("name");
        packageDTO.setPrice(BigDecimal.valueOf(9.99));
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(null);

        // Act & Assert
        Assertions.assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testGetPackageById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Package package1 = new Package();
        package1.setId(1L);
        package1.setName("Package 1");

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(package1));

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("Package 1");

        when(modelMapper.map(package1, PackageDTO.class)).thenReturn(packageDTO);

        PackageDTO result = packageService.getPackageById(1L);

        assertEquals(packageDTO.getId(), result.getId());
        assertEquals(packageDTO.getName(), result.getName());

        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(package1, PackageDTO.class);
    }

    @Test
    void testGetPackageById_NonExistingId() {
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> packageService.getPackageById(1L));

        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_NameMissing() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(BigDecimal.valueOf(100));
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }
    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidPiecesCarton() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(0);
        packageDTO.setAvailableQuantity(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(BigDecimal.valueOf(0));
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Pieces of carton must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidCartonId() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(BigDecimal.valueOf(100));
        packageDTO.setCartonId(1L);
        packageDTO.setEnglishName("en name");
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Carton does not exist with ID: " + packageDTO.getCartonId(), exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidEnglishName() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(BigDecimal.valueOf(100));
        packageDTO.setCartonId(1L);
        packageDTO.setEnglishName("тфуеуэ");
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("English name can contain only letters English", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }


    @Test
    void testCreatePackage_InvalidPackageDTO_PriceIsZero() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(BigDecimal.valueOf(0));
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_PriceIsNegative() {
        PackageDTO packageDTO = new PackageDTO();
        Carton carton = new Carton();
        carton.setId(1L);
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(1);
        packageDTO.setAvailableQuantity(1);
        packageDTO.setPrice(BigDecimal.valueOf(0));
        packageDTO.setPhoto("Photo");
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }


    @Test
    void testDeletePackage_ExistingId() throws ChangeSetPersister.NotFoundException {
        Package existingPackage = new Package();
        existingPackage.setId(1L);
        existingPackage.setDeleted(false);
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        packageService.deletePackage(1L);
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeletePackage_NonExistingId() {
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> packageService.deletePackage(1L));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
    }
}
