package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@Table(name = "carton")
public class Carton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
