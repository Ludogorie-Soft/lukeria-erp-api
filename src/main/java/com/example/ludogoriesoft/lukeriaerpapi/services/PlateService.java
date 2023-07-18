package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class PlateService {
    private final PlateRepository plateRepository;
    private final ModelMapper modelMapper;

    public List<PlateDTO> getAllPlates() {
        List<Plate> plates = plateRepository.findByDeletedFalse();
        return plates.stream()
                .map(plate -> modelMapper.map(plate, PlateDTO.class))
                .toList();
    }

    public PlateDTO getPlateById(Long id) throws ChangeSetPersister.NotFoundException {
        Plate plate = plateRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(plate, PlateDTO.class);
    }

    public PlateDTO createPlate(PlateDTO plateDTO) {
        if (StringUtils.isBlank(plateDTO.getName())) {
            throw new ValidationException("Name is required");
        }
        if (plateDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (plateDTO.getPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        Plate plateEntity = plateRepository.save(modelMapper.map(plateDTO, Plate.class));
        return modelMapper.map(plateEntity, PlateDTO.class);
    }

    public PlateDTO updatePlate(Long id, PlateDTO plateDTO) throws ChangeSetPersister.NotFoundException {
        Plate existingPlate = plateRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (StringUtils.isBlank(plateDTO.getName())) {
            throw new ValidationException("Name is required");
        }
        if (plateDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (plateDTO.getPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        existingPlate.setName(plateDTO.getName());
        existingPlate.setAvailableQuantity(plateDTO.getAvailableQuantity());
        existingPlate.setPrice(plateDTO.getPrice());
        Plate updatedPlate = plateRepository.save(existingPlate);
        updatedPlate.setId(id);
        return modelMapper.map(updatedPlate, PlateDTO.class);
    }

    public void deletePlate(Long id) throws ChangeSetPersister.NotFoundException {
        Plate plate = plateRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        plate.setDeleted(true);
        plateRepository.save(plate);
    }
}
