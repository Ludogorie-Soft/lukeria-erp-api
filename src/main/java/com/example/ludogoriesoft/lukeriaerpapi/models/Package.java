package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Моля въведете име на опаковката")
    private String name;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @ManyToOne
    @JoinColumn(name = "carton_id")
    private Carton cartonId;

    @Column(name = "pieces_carton")
    private int piecesCarton;

    private String photo;
    private double price;
}
