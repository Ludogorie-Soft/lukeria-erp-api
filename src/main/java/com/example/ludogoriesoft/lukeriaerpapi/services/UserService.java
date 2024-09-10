package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.beust.ah.A;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RefreshTokenBodyDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.TokenType;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.AuthenticationService;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.JwtService;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.TokenService;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.UserServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final TokenService tokenService;

    private void userValidations(UserDTO user) {
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
        if (StringUtils.isBlank(user.getPassword())) {
            throw new ValidationException("Password is required");
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

    public AuthenticationResponse updateAuthenticateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User existingUser = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        userValidations(userDTO);
        User updatedUser = modelMapper.map(userDTO, User.class);
        updatedUser.setId(existingUser.getId());
        userRepository.save(updatedUser);


        String jwtToken = jwtService.generateToken(updatedUser);
        String refreshToken = jwtService.generateRefreshToken(updatedUser);

        tokenService.revokeAllUserTokens(updatedUser);
        tokenService.saveToken(updatedUser, jwtToken, TokenType.ACCESS);
        tokenService.saveToken(updatedUser, refreshToken, TokenType.REFRESH);

        return AuthenticationResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(modelMapper.map(updatedUser, PublicUserDTO.class))
                .build();


    }

    public UserDTO updateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User existingUser = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        userValidations(userDTO);
        User updatedUser = modelMapper.map(userDTO, User.class);
        updatedUser.setId(existingUser.getId());
        userRepository.save(updatedUser);
        return modelMapper.map(updatedUser, UserDTO.class);

    }

    public boolean ifPasswordMatch(String password) {
        UserDTO authenticatedUserDTO = findAuthenticatedUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean ifMatch = passwordEncoder.matches(password, authenticatedUserDTO.getPassword());
        if (ifMatch) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePassword(UserDTO userDTO) {
        UserDTO authenticateUserDTO = findAuthenticatedUser();
        if (!(userDTO.getPassword().equals(userDTO.getRepeatPassword()))) {
            return false;
        }
        if (userDTO.getPassword().isEmpty()) {
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
