package com.example.ludogoriesoft.lukeriaerpapi.services;


import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ManufacturedProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ManufacturedProductService {

    private final ManufacturedProductRepository manufacturedProductRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ManufacturedProduct createManufacturedProduct(ManufacturedProduct manufacturedProduct) {
        manufacturedProduct.setManufacture_date(LocalDateTime.now()); // Set the current date
        return manufacturedProductRepository.save(manufacturedProduct);
    }

    public ManufacturedProduct createManufacturedProductFromProduct(Product product, int quantity, LocalDateTime manufacture_date, boolean deleted) {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(null, product, quantity, manufacture_date, deleted);
        manufacturedProduct.setManufacture_date(LocalDateTime.now()); // Set the current date
        return manufacturedProductRepository.save(manufacturedProduct);
    }

    public List<ManufacturedProductDTO> getAllManufacturedProducts() {
        List<ManufacturedProduct> manufacturedProducts = manufacturedProductRepository.findAll();
        return manufacturedProducts.stream()
                .map(product -> modelMapper.map(product, ManufacturedProductDTO.class)).toList();
    }

    public Optional<ManufacturedProduct> getManufacturedProductById(Long id) {
        return manufacturedProductRepository.findById(id);
    }

    public ManufacturedProduct updateManufacturedProduct(Long id, ManufacturedProductDTO updatedManufacturedProductDTO) {
        try {
            Optional<ManufacturedProduct> manufacturedProduct = manufacturedProductRepository.findById(id);
            manufacturedProduct.get().setProduct(productRepository.findById(updatedManufacturedProductDTO.getProductId()).orElse(null));
            manufacturedProduct.get().setQuantity(updatedManufacturedProductDTO.getQuantity());
            manufacturedProduct.get().setManufacture_date(updatedManufacturedProductDTO.getManufactureDate());
            manufacturedProduct.get().setDeleted(false);
            return manufacturedProductRepository.save(manufacturedProduct.get());
        } catch (Exception e) {
            throw new IllegalArgumentException("ManufacturedProduct with ID " + id + " not found.");
        }
    }

    public void deleteManufacturedProduct(Long id) {
        manufacturedProductRepository.findById(id).ifPresent(product -> {
            product.setDeleted(true);
            manufacturedProductRepository.save(product);
        });
    }
}
