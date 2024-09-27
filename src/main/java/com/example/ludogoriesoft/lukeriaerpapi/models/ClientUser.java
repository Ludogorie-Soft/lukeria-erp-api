package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client_user")
public class ClientUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client clientId;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;
    @Column(name = "is_deleted")
    private boolean deleted;
}
