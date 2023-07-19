package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProductClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderProductClientRepository extends JpaRepository<OrderProductClient,Long> {
    List<OrderProductClient> findByDeletedFalse();
    Optional<OrderProductClient> findByIdAndDeletedFalse(Long id);
}
