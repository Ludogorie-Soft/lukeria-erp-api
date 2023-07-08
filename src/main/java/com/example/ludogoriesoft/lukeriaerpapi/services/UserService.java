package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.UserMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO toDTO(User user) {
        return userMapper.toDto(user);
    }

    public User toEntity(UserDTO userDTO) {
        return userMapper.toEntity(userDTO);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(this::toDTO)
                .toList();
    }
    public UserDTO getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ApiRequestException("User with id: " + id + " Not Found");
        }
        return toDTO(optionalUser.get());
    }

    public UserDTO createUser(User user) {
        if (StringUtils.isBlank(user.getUsername())) {
            throw new ApiRequestException("User is blank");
        }
        userRepository.save(user);
        return toDTO(user);
    }

    public UserDTO updateUserWithoutPassword(Long id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ApiRequestException("User with id: " + id + " Not Found");
        }

        User existingUser = optionalUser.get();

        if (userDTO == null || userDTO.getUsername() == null || userDTO.getFullName()  == null
                || userDTO.getRole()== null||userDTO.getEmail()== null) {
            throw new ApiRequestException("Invalid User data!");
        }
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setRole(userDTO.getRole());

        User updatedUser = userRepository.save(existingUser);
        updatedUser.setId(id);
        return toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ApiRequestException("User not found for id " + id);
        }
        userRepository.delete(optionalUser.get());
    }



}
