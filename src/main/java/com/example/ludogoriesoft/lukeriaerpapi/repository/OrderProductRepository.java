package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {
    List<OrderProduct> findByDeletedFalse();
    Optional<OrderProduct> findByIdAndDeletedFalse(Long id);
}
