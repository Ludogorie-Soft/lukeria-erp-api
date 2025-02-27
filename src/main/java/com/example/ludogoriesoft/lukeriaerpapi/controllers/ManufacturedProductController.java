package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.ManufacturedProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manufactured-product")
@AllArgsConstructor
public class ManufacturedProductController {

    private final ManufacturedProductService manufacturedProductService;

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;


    @GetMapping
    public ResponseEntity<List<ManufacturedProductDTO>> getAllManufacturedProducts(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(manufacturedProductService.getAllManufacturedProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturedProductDTO> getManufacturedProductById(
            @PathVariable(name = "id") Long id,
            @RequestHeader("Authorization") String auth) {
        return manufacturedProductService.getManufacturedProductById(id)
                .map(product -> ResponseEntity.ok(modelMapper.map(product, ManufacturedProductDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ManufacturedProductDTO> createManufacturedProduct(
            @RequestBody ManufacturedProductDTO manufacturedProductDTO,
            @RequestHeader("Authorization") String auth) {
        ManufacturedProduct createdProduct = modelMapper.map(manufacturedProductDTO, ManufacturedProduct.class);
        manufacturedProductService.createManufacturedProduct(createdProduct);
        ManufacturedProductDTO dto = modelMapper.map(createdProduct, ManufacturedProductDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturedProductDTO> updateManufacturedProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ManufacturedProductDTO updatedManufacturedProductDTO,
            @RequestHeader("Authorization") String auth) {
        try {
            ManufacturedProduct updatedProduct = manufacturedProductService.updateManufacturedProduct(id, updatedManufacturedProductDTO);
            ManufacturedProductDTO dto = modelMapper.map(updatedProduct, ManufacturedProductDTO.class);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturedProduct(
            @PathVariable(name = "id") Long id,
            @RequestHeader("Authorization") String auth) {
        manufacturedProductService.deleteManufacturedProduct(id);
        return ResponseEntity.noContent().build();
    }

}
