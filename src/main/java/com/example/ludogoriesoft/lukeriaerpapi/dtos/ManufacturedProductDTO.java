package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturedProductDTO {
    private Long id;
    private Long Product;
    private int quantity;
    private LocalDateTime manufactureDate;

}
