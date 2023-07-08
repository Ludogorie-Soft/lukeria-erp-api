package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        if (product.getPlateId()==null){
            return new ProductDTO(
                    product.getId(),
                    product.getPrice(),
                    product.getAvailableQuantity(),
                    null
            );
        }
        return new ProductDTO(
                product.getId(),
                product.getPrice(),
                product.getAvailableQuantity(),
                product.getPlateId()
        );
    }

    public Product toEntity(ProductDTO productDTO) {
        Product entity = new Product();
        entity.setId(productDTO.getId());
        entity.setPrice(productDTO.getPrice());
        entity.setAvailableQuantity(productDTO.getAvailableQuantity());
        entity.setPlateId(productDTO.getPlateId());
        return entity;
    }
}
