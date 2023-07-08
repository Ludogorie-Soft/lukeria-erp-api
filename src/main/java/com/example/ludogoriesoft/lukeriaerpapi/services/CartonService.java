package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.CartonMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartonService {
    private final CartonRepository cartonRepository;
    private final CartonMapper mapper;

    //TODO да се обедини apiException за всички класове !
    public CartonDTO toDTO(Carton cartonEntity) {
        return mapper.toDto(cartonEntity);
    }

    public Carton toEntity(CartonDTO cartonDTO) {
        return mapper.toEntity(cartonDTO);
    }


    public List<CartonDTO> getAllCartons() {
        List<Carton> cartons = cartonRepository.findAll();
        return cartons
                .stream()
                .map(this::toDTO)
                .toList();
    }
    //TODO да се обедини apiException за всички класове !

    public CartonDTO getCartonById(Long id) {
        Optional<Carton> optionalCarton = cartonRepository.findById(id);
        if (optionalCarton.isEmpty()) {
            throw new ApiRequestException("Carton with id: " + id + " Not Found");
        }
        return toDTO(optionalCarton.get());
    }
    //TODO да се направят валидации за DTO преди Save!
    public CartonDTO createCarton(CartonDTO cartonDTO) {
        if (StringUtils.isBlank(cartonDTO.getName())) {
            throw new ApiRequestException("Carton is blank");
        }
        Carton cartonEntity = cartonRepository.save(toEntity(cartonDTO));
        return toDTO(cartonEntity);
    }

//TODO да се оправи метода update при CRUD
    public CartonDTO updateCarton(Long id, CartonDTO cartonDTO) {
        Optional<Carton> optionalCarton = cartonRepository.findById(id);
        if (optionalCarton.isEmpty()) {
            throw new ApiRequestException("Carton with id: " + id + " Not Found");
        }

        Carton existingCarton = optionalCarton.get();

        if (cartonDTO == null || cartonDTO.getName() == null || cartonDTO.getPrice() == 0
                || cartonDTO.getAvailableQuantity() == 0) {
            throw new ApiRequestException("Invalid Carton data!");
        }
        if (cartonDTO.getName() != null) {
            existingCarton.setName(cartonDTO.getName());
        }
        if (cartonDTO.getSize() != null) {
            existingCarton.setSize(cartonDTO.getSize());
        }
        if (cartonDTO.getAvailableQuantity() != 0) {
            existingCarton.setAvailableQuantity(cartonDTO.getAvailableQuantity());
        }
        if (cartonDTO.getPrice() != 0) {
            cartonDTO.setPrice(cartonDTO.getPrice());
        }
        Carton updatedCarton = cartonRepository.save(existingCarton);
        updatedCarton.setId(id);
        return toDTO(updatedCarton);
    }

    //TODO да се проучи SoftDelete 
    public void deleteCarton(Long id) {
        Optional<Carton> cartonOptional = cartonRepository.findById(id);
        if (cartonOptional.isEmpty()) {
            throw new ApiRequestException("Carton not found for id " + id);
        }
        cartonRepository.delete(cartonOptional.get());
    }
}
