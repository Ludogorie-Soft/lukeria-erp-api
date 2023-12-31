package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO {

    private Long id;
    @NotEmpty(message = "Името на опаковката е задължително за попълване!")
    private String name;
    @NotEmpty(message = "Името на опаковката на английски е задължително за попълване!")
    @Pattern(regexp = "^[a-zA-Z0-9\\s!@#$%^&*()-_=+]*$", message = "Името на опаковката на английски трябва да бъде само с латински букви!")
    private String englishName;
    @Min(value = 1, message = "Наличните бройки трябва да бъдат по-големи от 0!")
    private int availableQuantity;
    private Long cartonId;
    private Long plateId;
    @Min(value = 1, message = "Брой картони трябда да бъде по-голямо от 0!")
    private int piecesCarton;
    private String photo;
    @Min(value = 1, message = "Цената трябва да бъде по-голяма от 0!")
    private BigDecimal price;
    private String productCode;

}
