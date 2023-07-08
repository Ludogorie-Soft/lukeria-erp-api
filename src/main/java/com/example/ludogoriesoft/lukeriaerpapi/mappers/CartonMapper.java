package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import org.springframework.stereotype.Component;

@Component
public class CartonMapper {
    public CartonDTO toDto(Carton entity) {
        return new CartonDTO(
                entity.getId()
        );
    }

    public Carton toEntity(CartonDTO entity) {
        return new Carton(
                entity.getId()
        );
    }
}