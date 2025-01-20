package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManufacturedProductRepository extends JpaRepository<ManufacturedProduct, Long> {
    List<ManufacturedProduct> findByDeletedFalse();

    Optional<ManufacturedProduct> findByIdAndDeletedFalse(Long id);
}
