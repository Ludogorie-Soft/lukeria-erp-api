package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO {

    private Long id;
    @NotEmpty(message = "Името на опаковката е задължително за попълване!")
    private String name;
    @Min(value = 1, message = "Наличните бройки трябва да бъдат по-големи от 0!")
    private int availableQuantity;
    private Long cartonId;
    @Min(value = 1, message = "Бой картони трябда да бъде по-голямо от 0!")
    private int piecesCarton;
    private String photo;
    @Min(value = 1, message = "Цената трябва да бъде по-голяма от 0!")
    private double price;

}
