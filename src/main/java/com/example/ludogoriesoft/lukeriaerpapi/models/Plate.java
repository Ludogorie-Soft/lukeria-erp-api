package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "plate")
public class Plate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Моля въведете име на тарелката")
    private String name;
    @Column(name = "available_quantity")
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private Integer availableQuantity;
    private String photo;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private double price;
    @Column(name = "is_deleted")
    private boolean deleted;
}
