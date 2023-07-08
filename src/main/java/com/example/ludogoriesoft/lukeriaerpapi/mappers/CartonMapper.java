package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import org.springframework.stereotype.Component;

@Component
public class CartonMapper {
    public CartonDTO toDto(Carton carton) {
        return new CartonDTO(
                carton.getId(),
                carton.getName(),
                carton.getSize(),
                carton.getAvailableQuantity(),
                carton.getPrice()
        );
    }

    public Carton toEntity(CartonDTO cartonDTO) {
        Carton entity = new Carton();
        entity.setId(cartonDTO.getId());
        entity.setName(cartonDTO.getName());
        entity.setSize(cartonDTO.getSize());
        entity.setAvailableQuantity(cartonDTO.getAvailableQuantity());
        entity.setPrice(cartonDTO.getPrice());
        return entity;
    }
}