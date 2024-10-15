package com.example.ludogoriesoft.lukeriaerpapi.dtos;


import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotNull(message = "The name should not be null!")
    private String firstname;
    private String username;
    @NotNull(message = "The name should not be null!")
    private String lastname;
    private String password;
    private String repeatPassword;
    @Size(min = 5)
    private String address;
    @Email
    private String email;
    private Role role;
    private Long clientID;
}
