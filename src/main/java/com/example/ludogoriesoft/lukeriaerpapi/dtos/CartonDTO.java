package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartonDTO {
    private Long id;
    private String name;
    private String size;
    private Integer availableQuantity;
    private double price;
}
