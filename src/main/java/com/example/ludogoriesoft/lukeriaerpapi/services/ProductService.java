package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.ProductMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDTO toDTO(Product product) {
        return productMapper.toDto(product);
    }

    public Product toEntity(ProductDTO productDTO) {
        return productMapper.toEntity(productDTO);
    }


    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ApiRequestException("Plate with id: " + id + " Not Found");
        }
        return toDTO(optionalProduct.get());
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        if (StringUtils.isBlank(productDTO.getPlateId().getName())) {
            throw new ApiRequestException("Plate is blank");
        }
        Product product = productRepository.save(toEntity(productDTO));
        return toDTO(product);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ApiRequestException("Carton with id: " + id + " Not Found");
        }

        Product existingProduct = optionalProduct.get();

        if (productDTO == null || productDTO.getPlateId() == null || productDTO.getPrice() == 0
                || productDTO.getAvailableQuantity() == 0) {
            throw new ApiRequestException("Invalid Product data!");
        }
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setPlateId(productDTO.getPlateId());
        existingProduct.setAvailableQuantity(productDTO.getAvailableQuantity());
        Product updatedProduct = productRepository.save(existingProduct);
        updatedProduct.setId(id);
        return toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ApiRequestException("Product not found for id " + id);
        }
        productRepository.delete(optionalProduct.get());
    }


}
