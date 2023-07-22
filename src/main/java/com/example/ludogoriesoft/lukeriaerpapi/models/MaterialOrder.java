package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "material_order")
public class MaterialOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordered_quantity")
    private int orderedQuantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "material_type")
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @Column(name = "material_price")
    private BigDecimal materialPrice;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "is_deleted")
    private boolean deleted;
}
