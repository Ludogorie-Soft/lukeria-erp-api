package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PackageServiceTest {
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private CartonRepository cartonRepository;

    @Mock
    private ModelMapper modelMapper;

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
        packageDTO.setPrice(100);
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
        packageDTO.setPiecesCarton(-11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
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
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Carton does not exist with ID: " + packageDTO.getCartonId(), exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }
    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidCartonIdIsNull() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Carton ID cannot be null!", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_AvailableQuantityIsInvalid() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Available quantity be greater than zero", exception.getMessage());

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
        packageDTO.setPrice(0);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_PriceIsNegative() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPrice(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(-100);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }
    @Test
    void testCreatePackage_ValidPackage() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Test Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(100);
        packageDTO.setPrice(50.0);
        packageDTO.setCartonId(1L);

        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);

        Package packageEntity = new Package();
        packageEntity.setName("Test Package");
        packageEntity.setPiecesCarton(10);
        packageEntity.setAvailableQuantity(100);
        packageEntity.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        packageEntity.setCartonId(carton);

        Mockito.when(packageRepository.save(Mockito.any(Package.class))).thenReturn(packageEntity);
        Mockito.when(modelMapper.map(packageDTO, Package.class)).thenReturn(packageEntity);
        Mockito.when(modelMapper.map(packageEntity, PackageDTO.class)).thenReturn(packageDTO);

        PackageDTO result = packageService.createPackage(packageDTO);

        Assertions.assertEquals(packageDTO.getName(), result.getName());
        Assertions.assertEquals(packageDTO.getPiecesCarton(), result.getPiecesCarton());
        Assertions.assertEquals(packageDTO.getAvailableQuantity(), result.getAvailableQuantity());
        Assertions.assertEquals(packageDTO.getPrice(), result.getPrice());

        Mockito.verify(packageRepository).save(Mockito.any(Package.class));
    }
    @Test
    void testUpdatePackage_InvalidName() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_InvalidPiecesCarton() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(-11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_CartonIdNull() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(null);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_CartonIdDoesNotExist() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_InvalidPrice() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(-100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_InvalidAvailableQuantity() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(-10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdatePackage_ValidPackage() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(20);
        packageDTO.setAvailableQuantity(200);
        packageDTO.setPrice(100.0);
        packageDTO.setCartonId(2L);

        Mockito.when(packageRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(existingPackage));
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        Mockito.when(cartonRepository.findByIdAndDeletedFalse(packageDTO.getCartonId())).thenReturn(Optional.of(carton));

        Package updatedPackage = new Package();
        updatedPackage.setId(id);
        updatedPackage.setName(packageDTO.getName());
        updatedPackage.setPiecesCarton(packageDTO.getPiecesCarton());
        updatedPackage.setAvailableQuantity(packageDTO.getAvailableQuantity());
        updatedPackage.setPrice(packageDTO.getPrice());
        updatedPackage.setCartonId(carton);

        Mockito.when(packageRepository.save(existingPackage)).thenReturn(updatedPackage);
        Mockito.when(modelMapper.map(updatedPackage, PackageDTO.class)).thenReturn(packageDTO);

        // Act
        PackageDTO result = packageService.updatePackage(id, packageDTO);

        // Assert
        Assertions.assertEquals(packageDTO.getName(), result.getName());
        Assertions.assertEquals(packageDTO.getPiecesCarton(), result.getPiecesCarton());
        Assertions.assertEquals(packageDTO.getAvailableQuantity(), result.getAvailableQuantity());
        Assertions.assertEquals(packageDTO.getPrice(), result.getPrice());

        Mockito.verify(packageRepository).save(existingPackage);
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
