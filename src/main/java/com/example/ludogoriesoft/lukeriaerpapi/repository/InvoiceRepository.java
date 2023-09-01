package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findByDeletedFalse();
    Optional<Invoice> findByIdAndDeletedFalse(Long id);
    @Query("SELECT i.invoiceNumber FROM Invoice i WHERE i.invoiceNumber LIKE :prefix% ORDER BY i.invoiceNumber DESC")
    List<String> findLastInvoiceNumberStartingWith(@Param("prefix") String prefix);

}

