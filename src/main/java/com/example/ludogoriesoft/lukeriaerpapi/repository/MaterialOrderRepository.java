package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialOrderRepository extends JpaRepository<MaterialOrder,Long> {
    List<MaterialOrder> findByDeletedFalse();
    Optional<MaterialOrder> findByIdAndDeletedFalse(Long id);
}
