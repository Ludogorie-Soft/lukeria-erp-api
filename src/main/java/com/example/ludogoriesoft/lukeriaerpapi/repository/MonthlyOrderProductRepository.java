package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyOrderProductRepository extends JpaRepository<MonthlyOrderProduct,Long> {
    List<MonthlyOrderProduct> findByDeletedFalse();
    Optional<MonthlyOrderProduct> findByIdAndDeletedFalse(Long id);
}
