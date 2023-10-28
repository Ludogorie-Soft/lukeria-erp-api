package com.example.ludogoriesoft.lukeriaerpapi.services.security;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AdminUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RegisterRequest;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;

import java.util.List;

public interface UserServiceAuthentication {
    User createUser(RegisterRequest request);

    User findByEmail(String email);

    List<AdminUserDTO> getAllUsers();

    AdminUserDTO updateUser(Long id, AdminUserDTO userDTO, PublicUserDTO currentUser);

    void deleteUserById(Long id, PublicUserDTO currentUser);
}
