package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.ManufacturedProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/manufactured-product")
@AllArgsConstructor
public class ManufacturedProductController {

    private final ManufacturedProductService manufacturedProductService;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ManufacturedProductDTO>> getAllManufacturedProducts(@RequestHeader("Authorization") String auth) {
        List<ManufacturedProduct> manufacturedProducts = manufacturedProductService.getAllManufacturedProducts();
        List<ManufacturedProductDTO> manufacturedProductDTOS = manufacturedProducts.stream()
                .map(product -> modelMapper.map(product, ManufacturedProductDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(manufacturedProductDTOS);
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
            @RequestBody ManufacturedProduct manufacturedProduct,
            @RequestHeader("Authorization") String auth) {
        ManufacturedProduct createdProduct = manufacturedProductService.createManufacturedProduct(manufacturedProduct);
        ManufacturedProductDTO dto = modelMapper.map(createdProduct, ManufacturedProductDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/from-product")
    public ResponseEntity<ManufacturedProductDTO> createManufacturedProductFromProduct(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam boolean deleted,
            @RequestHeader("Authorization") String auth) {
        // Replace with actual product fetching logic
        Product product = productRepository.getById(productId);
        ManufacturedProduct createdProduct = manufacturedProductService.createManufacturedProductFromProduct(product, quantity, null, deleted);
        ManufacturedProductDTO dto = modelMapper.map(createdProduct, ManufacturedProductDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturedProductDTO> updateManufacturedProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ManufacturedProduct updatedManufacturedProduct,
            @RequestHeader("Authorization") String auth) {
        try {
            ManufacturedProduct updatedProduct = manufacturedProductService.updateManufacturedProduct(id, updatedManufacturedProduct);
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
