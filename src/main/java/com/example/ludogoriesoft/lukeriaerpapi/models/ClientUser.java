package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "client_user")
public class ClientUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long client_id;
    private Long user_id;
    @Column(name = "is_deleted")
    private boolean deleted;
}
