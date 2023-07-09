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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PlateServiceTest {
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
    public void testGetAllPlates() {
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
    public void testGetPlateById_ExistingId() throws ChangeSetPersister.NotFoundException {
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
    public void testGetPlateById_NonExistingId() {
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> plateService.getPlateById(1L));

        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testCreatePlate_InvalidPlateDTO_NameMissing() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(100.0);

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(plateRepository);
    }

    @Test
    public void testCreatePlate_InvalidPlateDTO_AvailableQuantityIsInvalid() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("some name");
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(0);
        plateDTO.setPrice(100.0);

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Available quantity must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(plateRepository);
    }

    @Test
    public void testCreatePlate_InvalidPlateDTO_PriceIsZero() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("some name");
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(0);

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(plateRepository);
    }

    @Test
    public void testCreatePlate_InvalidPlateDTO_PriceIsNegative() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setName("some name");
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(-10);

        ValidationException exception = assertThrows(ValidationException.class, () -> plateService.createPlate(plateDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(plateRepository);
    }

    @Test
    public void testUpdatePlate_MissingName() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setPhoto("photo");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(100.0);

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("Existing Plate");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(50.0);

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(ValidationException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }


    @Test
    public void testUpdatePlate_NotAvailableQuantity() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setPhoto("photo");
        plateDTO.setName("name");
        plateDTO.setAvailableQuantity(-10);
        plateDTO.setPrice(100.0);

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("name");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(50.0);

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(ValidationException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testUpdatePlate_InvalidPrice() {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setPhoto("photo");
        plateDTO.setName("name");
        plateDTO.setAvailableQuantity(10);
        plateDTO.setPrice(-100.0);

        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setName("name");
        existingPlate.setPhoto("photo");
        existingPlate.setAvailableQuantity(5);
        existingPlate.setPrice(50.0);

        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        assertThrows(ValidationException.class, () -> plateService.updatePlate(1L, plateDTO));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testDeletePlate_ExistingId() throws ChangeSetPersister.NotFoundException {
        Plate existingPlate = new Plate();
        existingPlate.setId(1L);
        existingPlate.setDeleted(false);
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPlate));
        plateService.deletePlate(1L);
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    public void testDeletePlate_NonExistingId() {
        when(plateRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> plateService.deletePlate(1L));
        verify(plateRepository, times(1)).findByIdAndDeletedFalse(1L);
    }
}

