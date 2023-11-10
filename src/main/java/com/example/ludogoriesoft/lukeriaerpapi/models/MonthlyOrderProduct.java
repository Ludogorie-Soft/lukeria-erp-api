package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "monthly_order_product")
public class MonthlyOrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package packageId;
    private Integer orderedQuantity;
    private Integer sentQuantity;
    @ManyToOne
    @JoinColumn(name = "monthly_order_id")
    private MonthlyOrder monthlyOrderId;
    @Column(name = "is_deleted")
    private boolean deleted;
}
