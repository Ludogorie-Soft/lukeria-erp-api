package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO {

    private Long id;
    private String name;
    private int availableQuantity;
    private Long cartonId;
    private int piecesCarton;
    private String photo;
    private double price;

}
