package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeletedFalse();
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }


    public UserDTO getUserById(Long id) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO createUser(User user) {
        if (StringUtils.isBlank(user.getUsername())) {
            throw new ValidationException("Username is required");
        }
        if (StringUtils.isBlank(user.getFirstname())) {
            throw new ValidationException("Full Name is required");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new ValidationException("Password is required");
        }
        if (StringUtils.isBlank(user.getRole().toString())) {
            throw new ValidationException("Role is required");
        }
        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User existingUser = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (StringUtils.isBlank(userDTO.getUsername())) {
            throw new ValidationException("Username is required");
        }
        if (StringUtils.isBlank(userDTO.getFullName())) {
            throw new ValidationException("Full Name is required");
        }
        if (StringUtils.isBlank(userDTO.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (StringUtils.isBlank(userDTO.getRole().toString())) {
            throw new ValidationException("Role is required");
        }
        existingUser.setUsernameField(userDTO.getUsername());
        existingUser.setFirstname(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setRole(userDTO.getRole());
        User updatedUser = userRepository.save(existingUser);
        updatedUser.setId(id);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public void deleteUser(Long id) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserDTO restoreUser(Long id) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        user.setDeleted(false);
        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }


}
