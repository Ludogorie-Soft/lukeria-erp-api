package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "carton")
public class Carton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Моля въведете името на кашона!")
    private String name;
    @NotNull(message = "Моля въведете размерите на кашона!")
    private String size;
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private Integer availableQuantity;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private double price;
    @Column(name = "is_deleted")
    private boolean deleted;
}
