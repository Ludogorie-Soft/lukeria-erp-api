package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private BigDecimal price;
    private Long packageId;
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private int availableQuantity;
    @Size(min = 2, max = 10, message = "Кода на продукта трябда да бъде между 2 и 10 символа!")
    private String productCode;
}
