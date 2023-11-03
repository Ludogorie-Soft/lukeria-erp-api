package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeletedFalse();

    Order findFirstByDeletedFalseOrderByIdDesc();

    Optional<Order> findByIdAndDeletedFalse(Long id);

}
