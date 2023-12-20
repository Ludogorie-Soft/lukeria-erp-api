package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "invoice")

public class Invoice {
    public static final Long FIRST_INVOICE_NUMBER = 2000000000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDate invoiceDate;
    private Long invoiceNumber;
    private BigDecimal totalPrice;
    private boolean isCashPayment;
    private LocalDate deadline;
    @Column(name = "is_deleted")
    private boolean deleted;
    @Column(name = "is_created")
    private boolean created;
    private String bankAccount;

}
