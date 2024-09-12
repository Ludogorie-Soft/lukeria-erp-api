package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerCustomPriceRepository extends JpaRepository<CustomerCustomPrice, Long> {
    List<CustomerCustomPrice> findByDeletedFalse();

    Optional<CustomerCustomPrice> findByIdAndDeletedFalse(Long id);
}
