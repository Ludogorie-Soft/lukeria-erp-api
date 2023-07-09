package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.PackageMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
// class PackageServiceTest {
//
//    @Mock
//    private PackageRepository packageRepository;
//
//    @Mock
//    private PackageMapper packageMapper;
//
//    @InjectMocks
//    private PackageService packageService;
//
//    @Test
//    void testToDTO() {
//        Package packageEntity = new Package();
//        packageEntity.setId(1L);
//        packageEntity.setName("Package 1");
//        packageEntity.setAvailableQuantity(10);
//
//        PackageDTO expectedDto = new PackageDTO();
//        expectedDto.setId(1L);
//        expectedDto.setName("Package 1");
//        expectedDto.setAvailableQuantity(10);
//
//        when(packageMapper.toDto(any(Package.class))).thenReturn(expectedDto);
//
//        PackageDTO resultDto = packageService.toDTO(packageEntity);
//
//        assertEquals(expectedDto, resultDto);
//    }
//
//    @Test
//    void testToEntity() {
//        PackageDTO packageDto = new PackageDTO();
//        packageDto.setId(1L);
//        packageDto.setName("Package 1");
//        packageDto.setAvailableQuantity(10);
//
//        Package expectedEntity = new Package();
//        expectedEntity.setId(1L);
//        expectedEntity.setName("Package 1");
//        expectedEntity.setAvailableQuantity(10);
//
//        when(packageMapper.toEntity(any(PackageDTO.class))).thenReturn(expectedEntity);
//
//        Package resultEntity = packageService.toEntity(packageDto);
//
//        assertEquals(expectedEntity, resultEntity);
//    }
//
//    @Test
//    void testGetAllPackages() {
//        // Създаване на списък от Package обекти за тестване
//        List<Package> packageList = new ArrayList<>();
//        Package package1 = new Package();
//        package1.setId(1L);
//        package1.setName("Package 1");
//        package1.setAvailableQuantity(10);
//        // Добавете останалите полета според вашите нужди
//        packageList.add(package1);
//
//        when(packageRepository.findAll()).thenReturn(packageList);
//
//        when(packageMapper.toDto(any(Package.class))).thenReturn(new PackageDTO());
//
//        List<PackageDTO> resultDtoList = packageService.getAllPackages();
//
//        assertNotNull(resultDtoList);
//
//        PackageDTO resultDto = resultDtoList.get(0);
//
//        assertNotNull(resultDto);
//
//    }
//
//
//    @Test
//    void testGetPackageById_ExistingPackage() {
//        Package packageEntity = new Package();
//        packageEntity.setId(999L);
//        packageEntity.setName("Package 1");
//        packageEntity.setAvailableQuantity(10);
//
//        when(packageRepository.findById(any(Long.class))).thenReturn(Optional.of(packageEntity));
//        when(packageMapper.toDto(any(Package.class))).thenReturn(new PackageDTO());
//
//        PackageDTO resultDto = packageService.getPackageById(999L);
//
//        assertNotNull(resultDto);
//
//    }
//
//    @Test
//    void testGetPackageById_NonExistingPackage() {
//        when(packageRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        assertThrows(ApiRequestException.class, () -> {
//            packageService.getPackageById(1L);
//        });
//    }
//
//    @Test
//    void testCreatePackage_BlankPackageName() {
//        // Създаване на PackageDTO с празно име за тестване
//        PackageDTO packageDto = new PackageDTO();
//        packageDto.setName(""); // Празно име
//
//        // Изчакване на хвърляне на ApiRequestException при извикване на метода
//        assertThrows(ApiRequestException.class, () -> packageService.createPackage(packageDto));
//
//        // Проверете дали методът packageRepository.save() не е бил извикан
//        verify(packageRepository, never()).save(any(Package.class));
//    }
//
//    @Test
//    void testDeletePackage_ValidId() {
//        // Създаване на валидно id за тестване
//        Long packageId = 1L;
//
//        // Мокване на поведението на packageRepository.findById
//        Package existingPackage = new Package();
//        existingPackage.setId(packageId);
//        when(packageRepository.findById(packageId)).thenReturn(Optional.of(existingPackage));
//
//        // Извикване на метода deletePackage с валидно id
//        packageService.deletePackage(packageId);
//
//        // Проверка дали packageRepository.findById е извикан с правилния id
//        verify(packageRepository).findById(packageId);
//
//        // Проверка дали packageRepository.delete е извикан с правилния пакет
//        verify(packageRepository).delete(existingPackage);
//    }
//
//    @Test
//    void testDeletePackage_InvalidId() {
//        Long invalidId = 999L;
//
//        when(packageRepository.findById(invalidId)).thenReturn(Optional.empty());
//
//        assertThrows(ApiRequestException.class, () -> packageService.deletePackage(invalidId));
//
//        verify(packageRepository).findById(invalidId);
//
//        verify(packageRepository, never()).delete(any(Package.class));
//    }
//
//    @Test
//    void testUpdatePackage_EmptyPackageDTO() {
//        // Подготовка на данни за тестване
//        Long id = 1L;
//        PackageDTO packageDto = null;
//
//
//        // Подготовка на мокнатите данни и поведение
//        Optional<Package> optionalPackage = Optional.of(new Package());
//        when(packageRepository.findById(id)).thenReturn(optionalPackage);
//
//        // Извикване на метода за тестване
//        assertThrows(ApiRequestException.class, () -> packageService.updatePackage(id, packageDto));
//
//        // Проверка на извикванията към репозиторията
//        verify(packageRepository, never()).save(any(Package.class));
//    }
//
//
//
//}
