package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartonDTO {
    private Long id;
    @NotNull(message = "Моля въведете името на кашона!")
    private String name;
    @NotNull(message = "Моля въведете размерите на кашона!")
    private String size;
    @Min(value = 1, message = "Наличните бройки трябва да бъдат по-големи от 0!")
    private Integer availableQuantity;
    private BigDecimal price;
}
