package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "package_id")
    private Package packageId;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private BigDecimal price;
    @Column(name = "available_quantity")
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private int availableQuantity;
    @Column(name = "is_deleted")
    private boolean deleted;
    @Size(min=2, max=10, message = "Кода на продукта трябда да бъде между 2 и 10 символа!")
    private String productCode;
}

