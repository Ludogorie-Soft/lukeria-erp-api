package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderDTO {

    private Long id;
    private LocalDateTime orderDate; // Add this if tracking order creation time
    private String status = "PENDING"; // e.g., PENDING, COMPLETED
    private LocalDate arrivalDate;
    private boolean deleted;

    // List of items in the order
    @Valid
    @NotEmpty(message = "At least one material item is required")
    private List<MaterialOrderItemDTO> items;
}
