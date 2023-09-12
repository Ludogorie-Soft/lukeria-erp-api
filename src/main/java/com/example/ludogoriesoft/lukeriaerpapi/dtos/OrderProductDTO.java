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
public class OrderProductDTO {
    private Long id;
    @NotNull
    @Min(value = 1, message = "Моля въведете поне 1 брой!")
    private Integer number;
    private Long orderId;
    private Long packageId;
    private BigDecimal sellingPrice;
}
