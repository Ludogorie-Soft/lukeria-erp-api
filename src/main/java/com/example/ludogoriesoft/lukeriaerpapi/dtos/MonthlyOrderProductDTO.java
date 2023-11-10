package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyOrderProductDTO {
    private Long id;
    private Long packageId;
    private Integer orderedQuantity;
    private Integer sentQuantity;
    private Long monthlyOrderId;
}
