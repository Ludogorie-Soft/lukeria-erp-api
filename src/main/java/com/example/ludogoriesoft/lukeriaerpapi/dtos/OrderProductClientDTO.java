package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductClientDTO {
    private Long id;
    @NotNull
    @Min(value = 1, message = "Моля въведете поне 1 брой!")
    private Integer number;
    private Order orderId;
    private Package packageId;
}
