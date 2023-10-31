package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyOrderDTO {
    private Long id;
    private Long clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean invoiced;
}
