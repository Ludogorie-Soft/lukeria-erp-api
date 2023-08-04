package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice_order_product")
public class InvoiceOrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoiceId;
    @ManyToOne
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProductId;

    @Column(name = "is_deleted")
    private boolean deleted;
}
