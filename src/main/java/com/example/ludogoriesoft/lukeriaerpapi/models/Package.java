package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Моля въведете име на опаковката")
    private String name;

    @Column(name = "available_quantity")
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private int availableQuantity;

    @ManyToOne
    @JoinColumn(name = "carton_id")
    private Carton cartonId;

    @Column(name = "pieces_carton")
    @Min(value = 1, message = "Полето не може да бъде с отрицателна стойност!")
    private int piecesCarton;

    private String photo;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private double price;
    @Column(name = "is_deleted")
    private boolean deleted;
}
