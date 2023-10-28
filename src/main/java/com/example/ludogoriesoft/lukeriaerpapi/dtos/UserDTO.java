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
//TODO: decide what you are gonna do with that dto
    private Long id;
    @NotNull(message = "Моля въведете потребителско име")
    @Size(min = 4)
    private String username;
    @Size(min = 5)
    @NotNull(message = "Моля въведете име и фамилия")
    private String fullName;
    @Email
    private String email;
    private Role role;


}
