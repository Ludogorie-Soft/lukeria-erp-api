package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderDTO {

    private Long id;
    @Min(1)
    private int orderedQuantity;
    private Integer receivedQuantity;
    @Min(1)
    private Long materialId;
    @NotBlank
    private String materialType;
    private BigDecimal materialPrice;
    private LocalDate arrivalDate;

}
