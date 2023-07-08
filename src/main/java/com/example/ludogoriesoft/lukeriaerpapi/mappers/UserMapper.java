package com.example.ludogoriesoft.lukeriaerpapi.mappers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDto(User user) {
        if (user.getRole() == null) {
            return new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    null
            );
        }
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public User toEntity(UserDTO userDTO) {
        User entity = new User();
        entity.setId(userDTO.getId());
        entity.setUsername(userDTO.getUsername());
        entity.setFullName(userDTO.getFullName());
        entity.setEmail(userDTO.getEmail());
        entity.setRole(userDTO.getRole());

        return entity;
    }
}
