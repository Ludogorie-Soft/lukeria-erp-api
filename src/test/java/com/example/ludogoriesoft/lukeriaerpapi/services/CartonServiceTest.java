package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

 class CartonServiceTest {
    @Mock
    private CartonRepository cartonRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartonService cartonService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetAllCartons() {
        Carton carton1 = new Carton();
        carton1.setId(1L);
        carton1.setName("Carton 1");

        Carton carton2 = new Carton();
        carton2.setId(2L);
        carton2.setName("Carton 2");

        List<Carton> mockCartons = Arrays.asList(carton1, carton2);
        when(cartonRepository.findByDeletedFalse()).thenReturn(mockCartons);

        CartonDTO cartonDTO1 = new CartonDTO();
        cartonDTO1.setId(1L);
        cartonDTO1.setName("Carton 1");

        CartonDTO cartonDTO2 = new CartonDTO();
        cartonDTO2.setId(2L);
        cartonDTO2.setName("Carton 2");

        when(modelMapper.map(carton1, CartonDTO.class)).thenReturn(cartonDTO1);
        when(modelMapper.map(carton2, CartonDTO.class)).thenReturn(cartonDTO2);

        List<CartonDTO> result = cartonService.getAllCartons();

        assertEquals(mockCartons.size(), result.size());
        assertEquals(mockCartons.get(0).getName(), result.get(0).getName());
        assertEquals(mockCartons.get(1).getName(), result.get(1).getName());

        verify(cartonRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockCartons.size())).map(any(Carton.class), eq(CartonDTO.class));
    }
    @Test
     void testGetCartonById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Carton carton = new Carton();
        carton.setId(1L);
        carton.setName("Carton 1");

        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(carton));

        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setId(1L);
        cartonDTO.setName("Carton 1");

        when(modelMapper.map(carton, CartonDTO.class)).thenReturn(cartonDTO);

        CartonDTO result = cartonService.getCartonById(1L);

        assertEquals(cartonDTO.getId(), result.getId());
        assertEquals(cartonDTO.getName(), result.getName());

        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(carton, CartonDTO.class);
    }

    @Test
     void testGetCartonById_NonExistingId() {
        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> cartonService.getCartonById(1L));

        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
     void testCreateCarton_InvalidCartonDTO_NameMissing() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setSize("Large");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(100.0);

        ValidationException exception = assertThrows(ValidationException.class, () -> cartonService.createCarton(cartonDTO));
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(cartonRepository);
    }
    @Test
     void testCreateCarton_InvalidCartonDTO_SizeMissing() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setName("some name");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(100.0);

        ValidationException exception = assertThrows(ValidationException.class, () -> cartonService.createCarton(cartonDTO));
        assertEquals("Size is required", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(cartonRepository);
    }
    @Test
     void testCreateCarton_InvalidCartonDTO_AvailableQuantityIsInvalid() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setName("some name");
        cartonDTO.setSize("12-18");
        cartonDTO.setAvailableQuantity(0);
        cartonDTO.setPrice(100.0);

        ValidationException exception = assertThrows(ValidationException.class, () -> cartonService.createCarton(cartonDTO));
        assertEquals("Available quantity must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(cartonRepository);
    }
    @Test
     void testCreateCarton_InvalidCartonDTO_PriceIsZero() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setName("some name");
        cartonDTO.setSize("12-18");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(0);

        ValidationException exception = assertThrows(ValidationException.class, () -> cartonService.createCarton(cartonDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(cartonRepository);
    }
    @Test
     void testCreateCarton_InvalidCartonDTO_PriceIsNegative() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setName("some name");
        cartonDTO.setSize("12-18");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(-10);

        ValidationException exception = assertThrows(ValidationException.class, () -> cartonService.createCarton(cartonDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(cartonRepository);
    }
    @Test
     void testUpdateCarton_MissingName() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setSize("Large");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(100.0);

        Carton existingCarton = new Carton();
        existingCarton.setId(1L);
        existingCarton.setName("Existing Carton");
        existingCarton.setSize("Medium");
        existingCarton.setAvailableQuantity(5);
        existingCarton.setPrice(50.0);

        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCarton));
        assertThrows(ValidationException.class, () -> cartonService.updateCarton(1L, cartonDTO));
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
     void testUpdateCarton_MissingSize() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setId(1L);
        cartonDTO.setName("name");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(100.0);

        Carton existingCarton = new Carton();
        existingCarton.setId(1L);
        existingCarton.setName("name");
        existingCarton.setSize("Existing Carton");
        existingCarton.setAvailableQuantity(5);
        existingCarton.setPrice(50.0);

        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCarton));
        assertThrows(ValidationException.class, () -> cartonService.updateCarton(1L, cartonDTO));
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
     void testUpdateCarton_NotAvailableQuantity() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setId(1L);
        cartonDTO.setSize("10-19");
        cartonDTO.setName("name");
        cartonDTO.setAvailableQuantity(-10);
        cartonDTO.setPrice(100.0);

        Carton existingCarton = new Carton();
        existingCarton.setId(1L);
        existingCarton.setName("name");
        existingCarton.setSize("Existing Carton");
        existingCarton.setAvailableQuantity(5);
        existingCarton.setPrice(50.0);

        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCarton));
        assertThrows(ValidationException.class, () -> cartonService.updateCarton(1L, cartonDTO));
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
     void testUpdateCarton_InvalidPrice() {
        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setId(1L);
        cartonDTO.setSize("10-19");
        cartonDTO.setName("name");
        cartonDTO.setAvailableQuantity(10);
        cartonDTO.setPrice(-100.0);

        Carton existingCarton = new Carton();
        existingCarton.setId(1L);
        existingCarton.setName("name");
        existingCarton.setSize("Existing Carton");
        existingCarton.setAvailableQuantity(5);
        existingCarton.setPrice(50.0);

        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCarton));
        assertThrows(ValidationException.class, () -> cartonService.updateCarton(1L, cartonDTO));
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
     void testDeleteCarton_ExistingId() throws ChangeSetPersister.NotFoundException {
        Carton existingCarton = new Carton();
        existingCarton.setId(1L);
        existingCarton.setDeleted(false);
        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCarton));
        cartonService.deleteCarton(1L);
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
     void testDeleteCarton_NonExistingId() {
        when(cartonRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> cartonService.deleteCarton(1L));
        verify(cartonRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
     void testUpdateCarton_ValidCarton() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long cartonId = 1L;
        Carton existingCarton = new Carton();
        existingCarton.setId(cartonId);
        existingCarton.setName("Carton 1");
        existingCarton.setSize("Size 1");
        existingCarton.setAvailableQuantity(10);
        existingCarton.setPrice(10.0);

        CartonDTO cartonDTO = new CartonDTO();
        cartonDTO.setName("Updated Carton 1");
        cartonDTO.setSize("Updated Size 1");
        cartonDTO.setAvailableQuantity(20);
        cartonDTO.setPrice(20.0);

        when(cartonRepository.findByIdAndDeletedFalse(cartonId)).thenReturn(Optional.of(existingCarton));
        when(modelMapper.map(existingCarton, CartonDTO.class)).thenReturn(cartonDTO);

        // Act
        Carton updatedCarton = new Carton();
        updatedCarton.setId(cartonId);
        when(cartonRepository.save(existingCarton)).thenReturn(updatedCarton);
        CartonDTO result = cartonService.updateCarton(cartonId, cartonDTO);


        verify(cartonRepository).save(existingCarton);
    }



}