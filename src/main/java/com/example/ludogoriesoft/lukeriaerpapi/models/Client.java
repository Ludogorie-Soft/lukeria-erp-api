package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String businessName;
    private String idNumEIK;
    private boolean hasIdNumDDS;
    private String address;
    private boolean isBulgarianClient;
    private String mol;
    @Column(name = "is_deleted")
    private boolean deleted;
}
