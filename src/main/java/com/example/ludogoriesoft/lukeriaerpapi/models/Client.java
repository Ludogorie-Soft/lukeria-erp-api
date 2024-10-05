package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String businessName;
    @Column(name = "english_business_name")
    private String englishBusinessName;
    private String idNumEIK;
    private boolean hasIdNumDDS;
    private String address;
    @Column(name = "english_address")
    private String englishAddress;
    private boolean isBulgarianClient;
    private String mol;
    @Column(name = "english_mol")
    private String englishMol;
    @Column(name = "is_deleted")
    private boolean deleted;
    private String contactPhone;
    private String deliveryAddress;
    private String information;
}
