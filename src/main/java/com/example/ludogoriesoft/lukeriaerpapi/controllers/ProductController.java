package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createPlate(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO cratedProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(cratedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductDTO productDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product with id: " + id + " has been deleted successfully!");
    }

    @PostMapping("/produce")
    public ResponseEntity<ProductDTO> produceProduct(@RequestParam("productId") Long productId, @RequestParam("producedQuantity") int producedQuantity) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productService.produceProduct(productId, producedQuantity));
    }
}
