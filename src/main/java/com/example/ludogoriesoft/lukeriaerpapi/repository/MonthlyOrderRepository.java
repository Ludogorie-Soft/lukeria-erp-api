package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyOrderRepository extends JpaRepository<MonthlyOrder,Long> {
    List<MonthlyOrder> findByDeletedFalse();
    Optional<MonthlyOrder> findByIdAndDeletedFalse(Long id);
}