package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "material_order")
public class MaterialOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date")
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "status")
    private String status = "PENDING"; // Default status

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "is_deleted")
    private boolean deleted;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialOrderItem> items = new ArrayList<>();
}
