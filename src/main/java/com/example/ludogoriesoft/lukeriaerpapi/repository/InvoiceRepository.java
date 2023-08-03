package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findByDeletedFalse();
    Optional<Invoice> findByIdAndDeletedFalse(Long id);
}
