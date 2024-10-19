package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order orderId;
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client clientId;
    @Column(name = "created_by_user")
    private Long createdByUser;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany
    private List<CartItem> items;
    @Column(name = "is_deleted")
    private boolean deleted;
}
