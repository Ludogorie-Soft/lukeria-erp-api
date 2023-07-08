package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.PlateMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlateService {
    private final PlateRepository plateRepository;
    private final PlateMapper mapper;

    public PlateDTO toDTO(Plate plateEntity) {
        return mapper.toDto(plateEntity);
    }

    public Plate toEntity(PlateDTO plateDTO) {
        return mapper.toEntity(plateDTO);
    }


    public List<PlateDTO> getAllPlates() {
        List<Plate> plates = plateRepository.findAll();
        return plates
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PlateDTO getPlateById(Long id) {
        Optional<Plate> optionalPlate = plateRepository.findById(id);
        if (optionalPlate.isEmpty()) {
            throw new ApiRequestException("Plate with id: " + id + " Not Found");
        }
        return toDTO(optionalPlate.get());
    }

    public PlateDTO createPlate(PlateDTO plateDTO) {
        if (StringUtils.isBlank(plateDTO.getName())) {
            throw new ApiRequestException("Plate is blank");
        }
        Plate PlateEntity = plateRepository.save(toEntity(plateDTO));
        return toDTO(PlateEntity);
    }


    public PlateDTO updatePlate(Long id, PlateDTO plateDTO) {
        Optional<Plate> optionalPlate = plateRepository.findById(id);
        if (optionalPlate.isEmpty()) {
            throw new ApiRequestException("Plate with id: " + id + " Not Found");
        }

        Plate existingPlate = optionalPlate.get();

        if (plateDTO == null || plateDTO.getName() == null || plateDTO.getPrice() == 0
                || plateDTO.getAvailableQuantity() == 0) {
            throw new ApiRequestException("Invalid Plate data!");
        }
        if (plateDTO.getName() != null) {
            existingPlate.setName(plateDTO.getName());
        }
        if (plateDTO.getAvailableQuantity() != 0) {
            existingPlate.setAvailableQuantity(plateDTO.getAvailableQuantity());
        }
        if (plateDTO.getPrice() != 0) {
            plateDTO.setPrice(plateDTO.getPrice());
        }
        if (plateDTO.getPhoto() != null) {
            plateDTO.setPhoto(plateDTO.getPhoto());
        }
        Plate updatedPlate = plateRepository.save(existingPlate);
        updatedPlate.setId(id);
        return toDTO(updatedPlate);
    }


    public void deletePlate(Long id) {
        Optional<Plate> plateOptional = plateRepository.findById(id);
        if (plateOptional.isEmpty()) {
            throw new ApiRequestException("Plate not found for id " + id);
        }
        plateRepository.delete(plateOptional.get());
    }
}
