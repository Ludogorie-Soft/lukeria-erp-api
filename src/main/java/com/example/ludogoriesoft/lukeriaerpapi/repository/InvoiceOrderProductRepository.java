package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.InvoiceOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceOrderProductRepository extends JpaRepository<InvoiceOrderProduct, Long> {

    List<InvoiceOrderProduct> findByDeletedFalse();

    Optional<InvoiceOrderProduct> findByIdAndDeletedFalse(Long id);
}
