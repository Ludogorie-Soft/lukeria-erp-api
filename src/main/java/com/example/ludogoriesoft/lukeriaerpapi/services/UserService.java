package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public void userValidations(UserDTO user) {
        if (StringUtils.isBlank(user.getUsername())) {
            throw new ValidationException("Username is required");
        }
        if (StringUtils.isBlank(user.getAddress())) {
            throw new ValidationException("Address is required");
        }
        if (StringUtils.isBlank(user.getFirstname())) {
            throw new ValidationException("First Name is required");
        }
        if (StringUtils.isBlank(user.getLastname())) {
            throw new ValidationException("Last name is required");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            throw new ValidationException("Email is required");
        }
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeletedFalse();
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }


    public UserDTO getUserById(Long id) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO createUser(UserDTO user) {
        userValidations(user);
        if (StringUtils.isBlank(user.getPassword())) {
            throw new ValidationException("Password is required");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User user1 = modelMapper.map(user, User.class);
        user1.setPassword(encodedPassword);
        user1.setUsernameField(user.getUsername());
        userRepository.save(user1);
        return modelMapper.map(user, UserDTO.class);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email"));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User existingUser = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        userValidations(userDTO);
        User updatedUser = modelMapper.map(userDTO, User.class);
        updatedUser.setPassword(existingUser.getPassword());
        updatedUser.setRole(existingUser.getRole());
        updatedUser.setUsernameField(existingUser.getUsernameField());
        updatedUser.setId(existingUser.getId());
        userRepository.save(updatedUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public boolean ifPasswordMatch(String password) {
        UserDTO authenticateUserDTO = findAuthenticatedUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       boolean ifMatch = passwordEncoder.matches(password,authenticateUserDTO.getPassword());
        if (ifMatch) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePassword(UserDTO userDTO) {
        UserDTO authenticateUserDTO = findAuthenticatedUser();
        if(!(userDTO.getPassword().equals(userDTO.getRepeatPassword()))){
            return false;
        }
        if(userDTO.getPassword().isEmpty()){
            return false;
        }
        User user = modelMapper.map(authenticateUserDTO, User.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return true;
    }

    public void deleteUser(Long id) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserDTO findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticateUser = findByEmail(email);
        return modelMapper.map(authenticateUser, UserDTO.class);
    }
}
