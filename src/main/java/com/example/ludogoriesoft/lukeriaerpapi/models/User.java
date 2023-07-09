package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Моля въведете потребителско име")
    @Size(min = 4)
    private String username;
    @Column(name = "full_name")
    @Size(min = 5)
    @NotNull(message = "Моля въведете име и фамилия")
    private String fullName;
    @Email
    private String email;
    @NotNull(message = "Моля въведете парола с поне 5 символа")
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "is_deleted")
    private boolean deleted;
}
