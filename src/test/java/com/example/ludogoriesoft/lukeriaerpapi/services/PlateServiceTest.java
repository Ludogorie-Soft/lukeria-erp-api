package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.PlateMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PlateServiceTest {

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private PlateMapper plateMapper;

    @InjectMocks
    private PlateService plateService;

    @Test
    void testToDTO() {
        Plate PlateEntity = new Plate();
        PlateEntity.setId(1L);
        PlateEntity.setName("Plate 1");
        PlateEntity.setAvailableQuantity(10);
        PlateDTO expectedDto = new PlateDTO();
        expectedDto.setId(1L);
        expectedDto.setName("Plate 1");
        expectedDto.setAvailableQuantity(10);
        when(plateMapper.toDto(any(Plate.class))).thenReturn(expectedDto);
        PlateDTO resultDto = plateService.toDTO(PlateEntity);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testToEntity() {
        PlateDTO PlateDto = new PlateDTO();
        PlateDto.setId(1L);
        PlateDto.setName("Plate 1");
        PlateDto.setAvailableQuantity(10);
        Plate expectedEntity = new Plate();
        expectedEntity.setId(1L);
        expectedEntity.setName("Plate 1");
        expectedEntity.setAvailableQuantity(10);
        when(plateMapper.toEntity(any(PlateDTO.class))).thenReturn(expectedEntity);
        Plate resultEntity = plateService.toEntity(PlateDto);
        assertEquals(expectedEntity, resultEntity);
    }

    @Test
    void testGetAllPlates() {
        List<Plate> PlateList = new ArrayList<>();
        Plate Plate1 = new Plate();
        Plate1.setId(1L);
        Plate1.setName("Plate 1");
        Plate1.setAvailableQuantity(10);
        PlateList.add(Plate1);
        when(plateRepository.findAll()).thenReturn(PlateList);
        when(plateMapper.toDto(any(Plate.class))).thenReturn(new PlateDTO());
        List<PlateDTO> resultDtoList = plateService.getAllPlates();
        assertNotNull(resultDtoList);
        PlateDTO resultDto = resultDtoList.get(0);
        assertNotNull(resultDto);

    }


    @Test
    void testGetPlateById_ExistingPlate() {
        Plate PlateEntity = new Plate();
        PlateEntity.setId(999L);
        PlateEntity.setName("Plate 1");
        PlateEntity.setAvailableQuantity(10);
        when(plateRepository.findById(any(Long.class))).thenReturn(Optional.of(PlateEntity));
        when(plateMapper.toDto(any(Plate.class))).thenReturn(new PlateDTO());
        PlateDTO resultDto = plateService.getPlateById(999L);
        assertNotNull(resultDto);

    }

    @Test
    void testGetPlateById_NonExistingPlate() {
        when(plateRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ApiRequestException.class, () -> {
            plateService.getPlateById(1L);
        });
    }

    @Test
    void testCreatePlate_BlankPlateName() {
        PlateDTO PlateDto = new PlateDTO();
        PlateDto.setName("");
        assertThrows(ApiRequestException.class, () -> plateService.createPlate(PlateDto));
        verify(plateRepository, never()).save(any(Plate.class));
    }

    @Test
    void testDeletePlate_ValidId() {
        Long PlateId = 1L;
        Plate existingPlate = new Plate();
        existingPlate.setId(PlateId);
        when(plateRepository.findById(PlateId)).thenReturn(Optional.of(existingPlate));
        plateService.deletePlate(PlateId);
        verify(plateRepository).findById(PlateId);
        verify(plateRepository).delete(existingPlate);
    }

    @Test
    void testDeletePlate_InvalidId() {
        Long invalidId = 999L;
        when(plateRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertThrows(ApiRequestException.class, () -> plateService.deletePlate(invalidId));
        verify(plateRepository).findById(invalidId);
        verify(plateRepository, never()).delete(any(Plate.class));
    }

    @Test
    void testUpdatePlate_EmptyPlateDTO() {
        Long id = 1L;
        PlateDTO PlateDto = null;
        Optional<Plate> optionalPlate = Optional.of(new Plate());
        when(plateRepository.findById(id)).thenReturn(optionalPlate);
        assertThrows(ApiRequestException.class, () -> plateService.updatePlate(id, PlateDto));
        verify(plateRepository, never()).save(any(Plate.class));
    }
}

