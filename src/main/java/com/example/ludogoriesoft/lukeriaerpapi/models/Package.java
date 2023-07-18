package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "available_quantity")
    private int availableQuantity;
    @ManyToOne
    @JoinColumn(name = "carton_id")
    private Carton cartonId;
    @ManyToOne
    @JoinColumn(name = "plate_id")
    private Plate plateId;
    @Column(name = "pieces_carton")
    private int piecesCarton;
    private String photo;
    private BigDecimal price;
    @Column(name = "is_deleted")
    private boolean deleted;
}