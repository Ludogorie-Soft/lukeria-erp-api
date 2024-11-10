package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();
    @Query("SELECT p FROM Product p WHERE p.forSale=true AND p.availableQuantity > 0 ")
    List<Product> getAvailableProductsForSale();

    Optional<Product> findByIdAndDeletedFalse(Long id);

    Optional<Product> findByPackageIdAndDeletedFalse(Package packageEntity);

    @Query("SELECT p FROM Product p WHERE p.forSale=true")
    List<Product> getProductsForSale();
    Product findByPackageId(Package packageId);

}
