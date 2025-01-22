package com.example.ludogoriesoft.lukeriaerpapi.services;


import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ManufacturedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ManufacturedProductService {

    private final ManufacturedProductRepository manufacturedProductRepository;

    @Autowired
    public ManufacturedProductService(ManufacturedProductRepository manufacturedProductRepository) {
        this.manufacturedProductRepository = manufacturedProductRepository;
    }

    public ManufacturedProduct createManufacturedProduct(ManufacturedProduct manufacturedProduct) {
        manufacturedProduct.setManufacture_date(LocalDateTime.now()); // Set the current date
        return manufacturedProductRepository.save(manufacturedProduct);
    }
    public ManufacturedProduct createManufacturedProductFromProduct(Product product, int quantity, LocalDateTime manufacture_date, boolean deleted) {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(null,product, quantity, manufacture_date, deleted);
        manufacturedProduct.setManufacture_date(LocalDateTime.now()); // Set the current date
        return manufacturedProductRepository.save(manufacturedProduct);
    }

    public List<ManufacturedProduct> getAllManufacturedProducts() {
        return manufacturedProductRepository.findAll();
    }

    public Optional<ManufacturedProduct> getManufacturedProductById(Long id) {
        return manufacturedProductRepository.findById(id);
    }

    public ManufacturedProduct updateManufacturedProduct(Long id, ManufacturedProduct updatedManufacturedProduct) {
        return manufacturedProductRepository.findById(id).map(existingProduct -> {
            existingProduct.setProduct(updatedManufacturedProduct.getProduct());
            existingProduct.setQuantity(updatedManufacturedProduct.getQuantity());
            existingProduct.setManufacture_date(updatedManufacturedProduct.getManufacture_date());
            existingProduct.setDeleted(updatedManufacturedProduct.isDeleted());
            return manufacturedProductRepository.save(existingProduct);
        }).orElseThrow(() -> new IllegalArgumentException("ManufacturedProduct with ID " + id + " not found."));
    }

    public void deleteManufacturedProduct(Long id) {
        manufacturedProductRepository.findById(id).ifPresent(product -> {
            product.setDeleted(true);
            manufacturedProductRepository.save(product);
        });
    }
}
