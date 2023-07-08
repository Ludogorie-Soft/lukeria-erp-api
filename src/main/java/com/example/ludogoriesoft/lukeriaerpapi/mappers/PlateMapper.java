package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import org.springframework.stereotype.Component;

@Component
public class PlateMapper {
    public PlateDTO toDto(Plate plate) {
        return new PlateDTO(
                plate.getId(),
                plate.getName(),
                plate.getAvailableQuantity(),
                plate.getPhoto(),
                plate.getPrice()
        );
    }

    public Plate toEntity(PlateDTO dto) {
        Plate entity = new Plate();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAvailableQuantity(dto.getAvailableQuantity());
        entity.setPhoto(dto.getPhoto());
        entity.setPrice(dto.getPrice());
        return entity;
    }
}
