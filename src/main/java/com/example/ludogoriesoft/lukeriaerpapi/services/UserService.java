package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.TokenType;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.EmailContentBuilder;
import com.example.ludogoriesoft.lukeriaerpapi.models.PasswordResetToken;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PasswordResetTokenRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.JwtService;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.TokenService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final EmailContentBuilder emailContentBuilder;
    private final String frontendUrl;


    public UserService(UserRepository userRepository,
                       ModelMapper modelMapper,
                       JwtService jwtService,
                       TokenService tokenService,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailService emailService,
                       EmailContentBuilder emailContentBuilder,
                       @Value("${frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.emailContentBuilder=emailContentBuilder;
        this.frontendUrl = frontendUrl;
    }


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

    private User update(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User existingUser = userRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        userValidations(userDTO);
        User updatedUser = modelMapper.map(userDTO, User.class);
        updatedUser.setId(existingUser.getId());
        return userRepository.save(updatedUser);
    }

    private AuthenticationResponse createNewToken(User user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenService.revokeAllUserTokens(user);
        tokenService.saveToken(user, jwtToken, TokenType.ACCESS);
        tokenService.saveToken(user, refreshToken, TokenType.REFRESH);

        return AuthenticationResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(modelMapper.map(user, PublicUserDTO.class))
                .build();
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
        return modelMapper.map(user1, UserDTO.class);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public AuthenticationResponse updateAuthenticateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User updatedUser = update(id, userDTO);
        return createNewToken(updatedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) throws ChangeSetPersister.NotFoundException {
        User updatedUser = update(id, userDTO);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public boolean ifPasswordMatch(String password) {
        UserDTO authenticatedUserDTO = findAuthenticatedUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, authenticatedUserDTO.getPassword());
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

    public boolean processForgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return false;
        }

        String token = UUID.randomUUID().toString();
        savePasswordResetToken(token, user);

        String body = emailContentBuilder.buildResetPasswordEmail(frontendUrl, token);

        emailService.sendHtmlEmailForForgotPassword(user.getEmail(), "Приложение на Лукерия ООД : Заявка за възстановяване на парола", body);

        return true;
    }

    private void savePasswordResetToken(String token, User user) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(resetToken);
    }

    public boolean updatePasswordWithToken(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            return false;
        }

        User user = resetToken.getUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}

