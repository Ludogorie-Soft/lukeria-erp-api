package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialOrderItemRepository extends JpaRepository<MaterialOrderItem, Long>{
}
