package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.CartonMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
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
class CartonServiceTest {

    @Mock
    private CartonRepository cartonRepository;

    @Mock
    private CartonMapper cartonMapper;

    @InjectMocks
    private CartonService cartonService;

    @Test
    void testToDTO() {
        Carton cartonEntity = new Carton();
        cartonEntity.setId(1L);
        cartonEntity.setName("Carton 1");
        cartonEntity.setAvailableQuantity(10);
        CartonDTO expectedDto = new CartonDTO();
        expectedDto.setId(1L);
        expectedDto.setName("Carton 1");
        expectedDto.setAvailableQuantity(10);
        when(cartonMapper.toDto(any(Carton.class))).thenReturn(expectedDto);
        CartonDTO resultDto = cartonService.toDTO(cartonEntity);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testToEntity() {
        CartonDTO cartonDto = new CartonDTO();
        cartonDto.setId(1L);
        cartonDto.setName("Carton 1");
        cartonDto.setAvailableQuantity(10);
        Carton expectedEntity = new Carton();
        expectedEntity.setId(1L);
        expectedEntity.setName("Carton 1");
        expectedEntity.setAvailableQuantity(10);
        when(cartonMapper.toEntity(any(CartonDTO.class))).thenReturn(expectedEntity);
        Carton resultEntity = cartonService.toEntity(cartonDto);
        assertEquals(expectedEntity, resultEntity);
    }

    @Test
    void testGetAllCartons() {
        List<Carton> cartonList = new ArrayList<>();
        Carton carton1 = new Carton();
        carton1.setId(1L);
        carton1.setName("Carton 1");
        carton1.setAvailableQuantity(10);
        cartonList.add(carton1);
        when(cartonRepository.findAll()).thenReturn(cartonList);
        when(cartonMapper.toDto(any(Carton.class))).thenReturn(new CartonDTO());
        List<CartonDTO> resultDtoList = cartonService.getAllCartons();
        assertNotNull(resultDtoList);
        CartonDTO resultDto = resultDtoList.get(0);
        assertNotNull(resultDto);

    }


    @Test
    void testGetCartonById_ExistingCarton() {
        Carton cartonEntity = new Carton();
        cartonEntity.setId(999L);
        cartonEntity.setName("Carton 1");
        cartonEntity.setAvailableQuantity(10);
        when(cartonRepository.findById(any(Long.class))).thenReturn(Optional.of(cartonEntity));
        when(cartonMapper.toDto(any(Carton.class))).thenReturn(new CartonDTO());
        CartonDTO resultDto = cartonService.getCartonById(999L);
        assertNotNull(resultDto);

    }

    @Test
    void testGetCartonById_NonExistingCarton() {
        when(cartonRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ApiRequestException.class, () -> {
            cartonService.getCartonById(1L);
        });
    }

    @Test
    void testCreateCarton_BlankCartonName() {
        CartonDTO cartonDto = new CartonDTO();
        cartonDto.setName(""); // Празно име
        assertThrows(ApiRequestException.class, () -> cartonService.createCarton(cartonDto));
        verify(cartonRepository, never()).save(any(Carton.class));
    }

    @Test
    void testDeleteCarton_ValidId() {
        // Създаване на валидно id за тестване
        Long cartonId = 1L;
        Carton existingCarton = new Carton();
        existingCarton.setId(cartonId);
        when(cartonRepository.findById(cartonId)).thenReturn(Optional.of(existingCarton));
        cartonService.deleteCarton(cartonId);
        verify(cartonRepository).findById(cartonId);
        verify(cartonRepository).delete(existingCarton);
    }

    @Test
    void testDeleteCarton_InvalidId() {
        Long invalidId = 999L;
        when(cartonRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertThrows(ApiRequestException.class, () -> cartonService.deleteCarton(invalidId));
        verify(cartonRepository).findById(invalidId);
        verify(cartonRepository, never()).delete(any(Carton.class));
    }

    @Test
    void testUpdateCarton_EmptyCartonDTO() {
        Long id = 1L;
        CartonDTO cartonDto = null;
        Optional<Carton> optionalCarton = Optional.of(new Carton());
        when(cartonRepository.findById(id)).thenReturn(optionalCarton);
        assertThrows(ApiRequestException.class, () -> cartonService.updateCarton(id, cartonDto));
        verify(cartonRepository, never()).save(any(Carton.class));
    }

    //TODO да се добавят всички тестове за Service
}

