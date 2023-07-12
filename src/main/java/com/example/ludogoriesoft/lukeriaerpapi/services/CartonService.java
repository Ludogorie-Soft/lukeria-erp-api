package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartonService {
    private final CartonRepository cartonRepository;
    private final ModelMapper modelMapper;

    public List<CartonDTO> getAllCartons() {
        List<Carton> cartons = cartonRepository.findByDeletedFalse();
        return cartons.stream().map(carton -> modelMapper.map(carton, CartonDTO.class)).toList();
    }

    public CartonDTO getCartonById(Long id) throws ChangeSetPersister.NotFoundException {
        Carton carton = cartonRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(carton, CartonDTO.class);
    }

    public CartonDTO createCarton(CartonDTO cartonDTO) {
        if (StringUtils.isBlank(cartonDTO.getName())) {
            throw new ValidationException("Name is required");
        }
        if (StringUtils.isBlank(cartonDTO.getSize())) {
            throw new ValidationException("Size is required");
        }
        if (cartonDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (cartonDTO.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than zero");
        }
        Carton cartonEntity = cartonRepository.save(modelMapper.map(cartonDTO, Carton.class));
        return modelMapper.map(cartonEntity, CartonDTO.class);
    }

    public CartonDTO updateCarton(Long id, CartonDTO cartonDTO) throws ChangeSetPersister.NotFoundException {
        Carton existingCarton = cartonRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (StringUtils.isBlank(cartonDTO.getName())) {
            throw new ValidationException("Name is required");
        }
        if (StringUtils.isBlank(cartonDTO.getSize())) {
            throw new ValidationException("Size is required");
        }
        if (cartonDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (cartonDTO.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than zero");
        }
        existingCarton.setName(cartonDTO.getName());
        existingCarton.setSize(cartonDTO.getSize());
        existingCarton.setAvailableQuantity(cartonDTO.getAvailableQuantity());
        existingCarton.setPrice(cartonDTO.getPrice());
        Carton updatedCarton = cartonRepository.save(existingCarton);
        updatedCarton.setId(id);
        return modelMapper.map(updatedCarton, CartonDTO.class);
    }

    public void deleteCarton(Long id) throws ChangeSetPersister.NotFoundException {
        Carton carton = cartonRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        carton.setDeleted(true);
        cartonRepository.save(carton);
    }
}
