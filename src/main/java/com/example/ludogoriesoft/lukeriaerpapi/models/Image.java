package com.example.ludogoriesoft.lukeriaerpapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID name;

    @CreationTimestamp
    private LocalDateTime uploadAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;
}
