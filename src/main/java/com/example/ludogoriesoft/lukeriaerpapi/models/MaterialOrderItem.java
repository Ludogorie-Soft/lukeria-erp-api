package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "material_order_item")
public class MaterialOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type")
    private MaterialType materialType;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "ordered_quantity")
    private int orderedQuantity;
    @Column(name = "received_quantity")
    private int receivedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private MaterialOrder order;
    private String materialName;
    private String photo;
}