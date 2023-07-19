package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "order_product_client_table ")
public class OrderProductClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Integer number;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order orderId;
    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package packageId;
    @Column(name = "is_deleted")
    private boolean deleted;
}
