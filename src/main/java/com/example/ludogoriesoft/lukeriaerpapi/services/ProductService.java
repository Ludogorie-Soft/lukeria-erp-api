package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByDeletedFalse();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    public ProductDTO getProductById(Long id) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productDTO.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than zero");
        }
        if (productDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        Product product = productRepository.save(modelMapper.map(productDTO, Product.class));
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ChangeSetPersister.NotFoundException {
        Product existingProduct = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (productDTO.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than zero");
        }
        if (productDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setAvailableQuantity(productDTO.getAvailableQuantity());
        Product updatedProduct = productRepository.save(existingProduct);
        updatedProduct.setId(id);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    public void deleteProduct(Long id) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        product.setDeleted(true);
        productRepository.save(product);
    }



}
