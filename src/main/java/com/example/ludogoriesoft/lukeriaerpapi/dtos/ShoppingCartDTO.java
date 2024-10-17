package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import com.example.ludogoriesoft.lukeriaerpapi.enums.OrderStatus;
import com.example.ludogoriesoft.lukeriaerpapi.models.CartItem;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long orderId;
    @NotNull
    private Long clientId;
    @NotNull
    private Long createdByUser;
    @NotEmpty
    private LocalDate orderDate;
    @NotEmpty
    private OrderStatus status;
    @NotEmpty
    private List<CartItem> items;
}
