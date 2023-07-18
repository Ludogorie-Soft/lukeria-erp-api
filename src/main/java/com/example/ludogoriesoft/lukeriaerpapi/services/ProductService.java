package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    private final PackageRepository packageRepository;
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
        validateProductDTO(productDTO);

        Product product = productRepository.save(modelMapper.map(productDTO, Product.class));
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ChangeSetPersister.NotFoundException {
        validateProductDTO(productDTO);

        Product existingProduct = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Product updatedProduct = modelMapper.map(productDTO, Product.class);
        updatedProduct.setId(existingProduct.getId());
        productRepository.save(updatedProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private void validateProductDTO(ProductDTO productDTO) {
        if (productDTO.getPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        if (productDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (productDTO.getPackageId() != null) {
            boolean existsPackage = packageRepository.existsById(productDTO.getPackageId());
            if (!existsPackage) {
                throw new ValidationException("Package does not exist with ID: " + productDTO.getPackageId());
            }
        } else {
            throw new ValidationException("Package ID cannot be null");
        }
    }
    public void deleteProduct(Long id) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        product.setDeleted(true);
        productRepository.save(product);
    }
}
