package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlateServiceTest {
    @Mock
    private PlateRepository plateRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlateService plateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPlates() {
        Plate plate1 = new Plate();
        plate1.setId(1L);
        plate1.setName("Plate 1");

        Plate plate2 = new Plate();
        plate2.setId(2L);
        plate2.setName("Plate 2");

        List<Plate> mockPlates = Arrays.asList(plate1, plate2);
        when(plateRepository.findByDeletedFalse()).thenReturn(mockPlates);

        PlateDTO plateDTO1 = new PlateDTO();
        plateDTO1.setId(1L);
        plateDTO1.setName("Plate 1");

        PlateDTO plateDTO2 = new PlateDTO();
        plateDTO2.setId(2L);
        plateDTO2.setName("Plate 2");

        when(modelMapper.map(plate1, PlateDTO.class)).thenReturn(plateDTO1);
        when(modelMapper.map(plate2, PlateDTO.class)).thenReturn(plateDTO2);

        List<PlateDTO> result = plateService.getAllPlates();

        assertEquals(mockPlates.size(), result.size());
        assertEquals(mockPlates.get(0).getName(), result.get(0).getName());
        assertEquals(mockPlates.get(1).getName(), result.get(1).getName());

        verify(plateRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockPlates.size())).map(any(Plate.class), eq(PlateDTO.class));
    }

    @Test
    void testGetPlateById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Plate plate = new Plate();
        plate.setId(1L);
        plate.setName("Plate 1");

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(plate));

        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setName("Plate 1");

        when(modelMapper.map(plate, PlateDTO.class)).thenReturn(plateDTO);

        PlateDTO result = plateService.getPlateById(1L);

        assertEquals(plateDTO.getId(), result.getId());
        assertEquals(plateDTO.getName(), result.getName());

        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(plate, PlateDTO.class);
    }

    @Test
    void testGetPlateById_NonExistingId() {
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> plateService.getPlateById(1L));

        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreatePlate_ValidPlate() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("Plate 1");
        plateDTO.setPhoto("Photo 1");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(BigDecimal.valueOf(10.0));

        Plate plateEntity = new Plate();
        plateEntity.setName("Plate 1");
        plateEntity.setPhoto("Photo 1");
        plateEntity.setAvailableQuantity(10);
        plateEntity.setPrice(BigDecimal.valueOf(10.0));

        when(plateRepository.save(any(Plate.class))).thenReturn(plateEntity);
        when(modelMapper.map(plateDTO, Plate.class)).thenReturn(plateEntity);
        when(modelMapper.map(plateEntity, PlateDTO.class)).thenReturn(plateDTO);

        // Act
        PlateDTO result = plateService.createPlate(plateDTO);

        // Assert
        assertEquals(plateDTO.getName(), result.getName());
        assertEquals(plateDTO.getPhoto(), result.getPhoto());
        assertEquals(plateDTO.getAvailableQuantity(), result.getAvailableQuantity());
        assertEquals(plateDTO.getPrice(), result.getPrice());

        // Verify that plateRepository.save() is called with the expected Plate object
        verify(plateRepository).save(plateEntity);
    }

    @Test
    void testCreatePlate_InvalidPlateDTO_NameMissing() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(BigDecimal.valueOf(100.0));

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(plateRepository);
    }

    @Test
    void testCreatePlate_InvalidPlateDTO_AvailableQuantityIsInvalid() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("some name");
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(0);
        plateDTO.setPrice(BigDecimal.valueOf(100.0));

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Available quantity must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(plateRepository);
    }

    @Test
    void testCreatePlate_InvalidPlateDTO_PriceIsZero() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("some name");
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(BigDecimal.valueOf(0));

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(plateRepository);
    }


    @Test
    void testUpdatePlate_ValidPlate() throws ChangeSetPersister.NotFoundException {
        Long plateId = 1L;
        Plate existingPlate = new Plate();
        existingPlate.setId(plateId);
        existingPlate.setName("Plate 1");
        existingPlate.setPhoto("Photo 1");
        existingPlate.setAvailableQuantity(10);
        existingPlate.setPrice(BigDecimal.valueOf(10.0));

        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("Updated Plate 1");
        plateDTO.setPhoto("Updated Photo 1");
        plateDTO.setAvailableQuantity(20);
        plateDTO.setPrice(BigDecimal.valueOf(20.0));

        when(plateRepository.findByIdAndDeletedFalse(plateId)).thenReturn(Optional.of(existingPlate));
        when(modelMapper.map(existingPlate, PlateDTO.class)).thenReturn(plateDTO);

        Plate updatedPlate = new Plate();
        updatedPlate.setId(plateId);
        when(plateRepository.save(existingPlate)).thenReturn(updatedPlate);
        PlateDTO result = plateService.updatePlate(plateId, plateDTO);

        verify(plateRepository).save(existingPlate);
    }


    @Test
    void testUpdatePlate_MissingName() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(BigDecimal.valueOf(100.0));

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("Existing Plate");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(BigDecimal.valueOf(50.0));

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(ValidationException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }


    @Test
    void testUpdatePlate_NotAvailableQuantity() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setPhoto("photo");
        plateDTO.setName("name");
        plateDTO.setAvailableQuantity(-10);
        plateDTO.setPrice(BigDecimal.valueOf(100.0));

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("name");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(BigDecimal.valueOf(50.0));

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(ValidationException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePlate_InvalidPrice() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setPhoto("photo");
        plateDTO.setName("name");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(BigDecimal.valueOf(-100.0));

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("name");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(BigDecimal.valueOf(50.0));

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(NullPointerException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeletePlate_ExistingId() throws ChangeSetPersister.NotFoundException {
        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setDeleted(false);
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        plateService.deletePlate(1L);
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeletePlate_NonExistingId() {
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> plateService.deletePlate(1L));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
    }
}

