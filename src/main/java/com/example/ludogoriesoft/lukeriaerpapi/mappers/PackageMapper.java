package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import org.springframework.stereotype.Component;

@Component
public class PackageMapper {

    public PackageDTO toDto(Package entity) {
        if (entity.getCartonId()==null){
             return new PackageDTO(
                    entity.getId(),
                    entity.getName(),
                    entity.getAvailableQuantity(),
                    null,
                    entity.getPiecesCarton(),
                    entity.getPhoto(),
                    entity.getPrice()
            );
        }
        return new PackageDTO(
                entity.getId(),
                entity.getName(),
                entity.getAvailableQuantity(),
                entity.getCartonId().getId(),
                entity.getPiecesCarton(),
                entity.getPhoto(),
                entity.getPrice()
        );
    }

    public Package toEntity(PackageDTO dto) {
        Package entity = new Package();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
//        entity.setCartonId(dto.getCartonId());
        entity.setAvailableQuantity(dto.getAvailableQuantity());
        entity.setPiecesCarton(dto.getPiecesCarton());
        entity.setPhoto(dto.getPhoto());
        entity.setPrice(dto.getPrice());
        return entity;
    }


}
