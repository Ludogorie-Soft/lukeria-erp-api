package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByDeletedFalse();

    Optional<Invoice> findByIdAndDeletedFalse(Long id);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i")
    Long findLastInvoiceNumber();
    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE '1%'")
    Long findLastInvoiceNumberAbroad();

}

