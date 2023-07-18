package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "carton")
public class Carton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String size;
    private Integer availableQuantity;
    private BigDecimal price;
    @Column(name = "is_deleted")
    private boolean deleted;
}
