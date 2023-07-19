package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    @NotNull
    private Long clientId;
    private LocalDate orderDate;
}
