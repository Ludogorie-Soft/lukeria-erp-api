package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.enums.TokenType;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.EmailContentBuilder;
import com.example.ludogoriesoft.lukeriaerpapi.models.PasswordResetToken;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PasswordResetTokenRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.JwtService;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.TokenService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User existingUser;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailContentBuilder emailContentBuilder;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;


    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsernameField("existingUser");
        existingUser.setDeleted(false);
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // Set other properties as needed
    }

    @Test
    void getAllUsers() {
        when(userRepository.findByDeletedFalse()).thenReturn(Collections.singletonList(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(new UserDTO());
        List<UserDTO> userDTOList = userService.getAllUsers();
        assertFalse(userDTOList.isEmpty());
        assertEquals(1, userDTOList.size());
    }

    @Test
    void getUserById_userExists() throws UserNotFoundException, ChangeSetPersister.NotFoundException {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(new UserDTO());
        UserDTO userDTO = userService.getUserById(1L);
        assertNotNull(userDTO);
        assertEquals(existingUser.getUsername(), userDTO.getUsername());
    }

    @Test
    void findByEmail_userExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        User user = userService.findByEmail("test@example.com");
        assertNotNull(user);
    }

    @Test
    void findByEmail_userNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("nonexistent@example.com"));
    }

    @Test
    void updateUser_validUser() throws ChangeSetPersister.NotFoundException {
        // Setup initial data
        User existingUser = new User(1L, "bel", "bel", "bel@gmail.com", "pass", "address", "user", Role.ADMIN, false);
        UserDTO userDTO = new UserDTO(1L, "bel", "bel", "bel@gmail.com", "pass", "pass", "address", "user", Role.ADMIN);
        User updatedUser = new User(1L, "bel", "bel", "bel@gmail.com", "pass", "address", "user", Role.ADMIN, false);

        // Mock repository behavior
        when(userRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Mock modelMapper behavior
        when(modelMapper.map(any(UserDTO.class), any())).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), any())).thenReturn(userDTO);

        // Call the service method
        UserDTO result = userService.updateUser(1L, userDTO);

        // Validate the results
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getAddress(), result.getAddress());
        assertEquals(userDTO.getRole(), result.getRole());
    }

    @Test
    void updateUser_userNotFound() {
        when(userRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> userService.updateUser(2L, new UserDTO()));
    }

    @Test
    void createUser_NullUsername_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullFirstName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullLastName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullAddress_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullEmail_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        userDTO.setAddress("address");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullPassword_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        userDTO.setAddress("address");
        userDTO.setEmail("email@gmail.com");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void updateUser_NullUsername_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullAddress_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullFirstName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullLastName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        userDTO.setFirstname("firstName");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullEmail_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        userDTO.setFirstname("firstName");
        userDTO.setLastname("lastname");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void deleteUser_UserExists_UserDeletedSuccessfully() {
        Long userId = 1L;
        User userToDelete = new User();
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(userToDelete));

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).save(userToDelete);
        assertTrue(userToDelete.isDeleted());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findAuthenticatedUser_UserExists() {
        // Setup mock for the SecurityContextHolder and Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication to return the email
        when(authentication.getName()).thenReturn("authenticated@example.com");

        // Create a mock authenticated user and mock repository and modelMapper behavior
        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail("authenticated@example.com");
        authenticatedUser.setUsernameField("authUser");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("authenticated@example.com");

        // Mock the findByEmail method
        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(authenticatedUser));

        // Mock modelMapper behavior
        when(modelMapper.map(authenticatedUser, UserDTO.class)).thenReturn(userDTO);

        // Call the service method
        UserDTO result = userService.findAuthenticatedUser();

        // Validate the results
        assertNotNull(result);
        assertEquals("authenticated@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("authenticated@example.com");
    }

    @Test
    void createUser_ValidUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setFirstname("name");
        userDTO.setLastname("lastName");
        userDTO.setPassword("testPassword123");
        userDTO.setEmail("test@example.com");
        userDTO.setAddress("Address");

        User user = new User();
        user.setUsernameField("testUser");
        user.setPassword("testPassword123");
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);


        BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
        when(passwordEncoder.encode("testPassword123")).thenReturn("encodedPassword123");

        when(userRepository.save(any(User.class))).thenReturn(user);

        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);
        verify(userRepository, times(1)).save(any(User.class)); // Ensure the repository's save method was called
    }

    @Test
    void findAuthenticatedUser_UserNotFound() {
        // Setup mock for the SecurityContextHolder and Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication to return an email that doesn't exist in the repository
        when(authentication.getName()).thenReturn("nonexistent@example.com");

        // Mock the findByEmail method to throw UserNotFoundException
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Assert that UserNotFoundException is thrown
        assertThrows(UserNotFoundException.class, () -> userService.findAuthenticatedUser());

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void updateAuthenticateUser_Success() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setUsername("testUser");
        userDTO.setAddress("123 Test Street");   // Set required address field
        userDTO.setFirstname("John");            // Set required firstname field
        userDTO.setLastname("Doe");              // Set required lastname field
        userDTO.setPassword("password123");      // Set required password field

        // Mock existing user in the database
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsernameField("oldUser");

        // Mock updated user (result of mapping userDTO to User entity)
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("test@example.com");
        updatedUser.setUsernameField("testUser");

        // Mock JWT and refresh token generation
        String jwtToken = "newJwtToken";
        String refreshToken = "newRefreshToken";

        // Mock repository and service behavior
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(userDTO, User.class)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);  // Simulate user saving
        when(jwtService.generateToken(updatedUser)).thenReturn(jwtToken);
        when(jwtService.generateRefreshToken(updatedUser)).thenReturn(refreshToken);
        when(modelMapper.map(updatedUser, PublicUserDTO.class)).thenReturn(new PublicUserDTO());

        // Act
        AuthenticationResponse response = userService.updateAuthenticateUser(userId, userDTO);

        // Assert
        assertNotNull(response);                                     // Ensure the response is not null
        assertEquals(jwtToken, response.getAccessToken());           // Validate access token
        assertEquals(refreshToken, response.getRefreshToken());      // Validate refresh token
        verify(userRepository, times(1)).save(updatedUser);          // Ensure user is saved
        verify(tokenService, times(1)).revokeAllUserTokens(updatedUser); // Ensure tokens are revoked
        verify(tokenService, times(1)).saveToken(updatedUser, jwtToken, TokenType.ACCESS);  // Save access token
        verify(tokenService, times(1)).saveToken(updatedUser, refreshToken, TokenType.REFRESH);  // Save refresh token
    }

    @Test
    void updateAuthenticateUser_UserNotFound() {
        // Arrange
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();

        // Mock repository to return empty (user not found)
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> userService.updateAuthenticateUser(userId, userDTO));

        verify(userRepository, never()).save(any(User.class)); // Ensure save is never called
        verify(tokenService, never()).revokeAllUserTokens(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    void ifPasswordMatch_ReturnsTrue_WhenPasswordMatches() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("authenticated@example.com");
        // Arrange
        String username = "authenticated@example.com";
        String rawPassword = "password123";
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);


        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail(username);
        authenticatedUser.setPassword(encodedPassword);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(username);
        userDTO.setPassword(encodedPassword);


        // Mock the findByEmail method
        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(authenticatedUser));

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        // Mock modelMapper behavior
        when(modelMapper.map(authenticatedUser, UserDTO.class)).thenReturn(userDTO);

        // Act
        boolean result = userService.ifPasswordMatch(rawPassword);

        // Assert
        assertTrue(result, "The password should match");
    }

    @Test
    void ifPasswordMatch_ReturnsFalse_WhenPasswordDoesNotMatch() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("authenticated@example.com");
        // Arrange
        String rawPassword = "password123";
        String wrongPassword = "wrongPassword";
        String username = "authenticated@example.com";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword); // Encode the correct password

        UserDTO authenticatedUserDTO = new UserDTO();
        authenticatedUserDTO.setPassword(encodedPassword);
        authenticatedUserDTO.setUsername(username);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail(username);
        authenticatedUser.setPassword(encodedPassword);

        // Mock the findByEmail method
        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(authenticatedUser));


        when(userRepository.findByEmail(username)).thenReturn(Optional.of(authenticatedUser));
        // Mock the behavior of findAuthenticatedUser to return the authenticatedUserDTO with encoded password
        when(userService.findAuthenticatedUser()).thenReturn(authenticatedUserDTO);

        // Act
        boolean result = userService.ifPasswordMatch(wrongPassword);

        // Assert
        assertFalse(result, "The password should not match");
    }

    @Test
    void updatePassword_ReturnsFalse_WhenPasswordsDoNotMatch() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("authenticated@example.com");
        String username = "authenticated@example.com";
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("password123");
        userDTO.setRepeatPassword("differentPassword");

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail(username);
        authenticatedUser.setPassword("password123");

        UserDTO authenticatedUserDTO = new UserDTO();
        authenticatedUserDTO.setEmail(username);

        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(authenticatedUser));

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(authenticatedUser));
        when(userService.findAuthenticatedUser()).thenReturn(authenticatedUserDTO);

        boolean result = userService.updatePassword(userDTO);

        assertFalse(result, "The passwords do not match; the update should not succeed.");
    }

    @Test
    void updatePassword_ReturnsFalse_WhenPasswordIsEmpty() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("authenticated@example.com");
        String username = "authenticated@example.com";
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("");
        userDTO.setRepeatPassword("");

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail(username);
        authenticatedUser.setPassword("");

        UserDTO authenticatedUserDTO = new UserDTO();
        authenticatedUserDTO.setEmail(username);

        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(authenticatedUser));

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(authenticatedUser));
        when(userService.findAuthenticatedUser()).thenReturn(authenticatedUserDTO);
        boolean result = userService.updatePassword(userDTO);

        assertFalse(result, "The password is empty; the update should not succeed.");
    }

    @Test
    void updatePassword_ReturnsTrue_WhenPasswordUpdateIsSuccessful() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("authenticated@example.com");
        String username = "authenticated@example.com";
        String rawPassword = "password123";
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

        UserDTO userDTO = new UserDTO();
        userDTO.setPassword(rawPassword);
        userDTO.setRepeatPassword(rawPassword);

        UserDTO authenticatedUserDTO = new UserDTO();
        authenticatedUserDTO.setEmail(username);

        User user = new User();
        user.setEmail(username);

        when(userRepository.findByEmail("authenticated@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(userService.findAuthenticatedUser()).thenReturn(authenticatedUserDTO);

        when(modelMapper.map(authenticatedUserDTO, User.class)).thenReturn(user);

        boolean result = userService.updatePassword(userDTO);

        assertTrue(result, "The password update should succeed.");

        verify(userRepository).save(user);
        assertTrue(bCryptPasswordEncoder.matches(rawPassword, user.getPassword()));
    }
    @Test
    void processForgotPassword_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.processForgotPassword("nonexistent@example.com");
        });

        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void processForgotPassword_UserExists_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String token = UUID.randomUUID().toString();


        String resetEmailContent = "Reset your password using this link: " + token;
        when(emailContentBuilder.buildResetPasswordEmail(anyString(), eq(token))).thenReturn(resetEmailContent);

        doNothing().when(emailService).sendHtmlEmail(eq(user.getEmail()), anyString(), eq(resetEmailContent));

        boolean result = userService.processForgotPassword("test@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetTokenRepository, times(1)).save(any());
    }

    @Test
    void updatePasswordWithToken_ValidToken_Success() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        User user = new User();
        user.setPassword("oldPassword123");
        resetToken.setUser(user);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(resetToken);

        boolean result = userService.updatePasswordWithToken(token, newPassword);

        assertTrue(result);

        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void updatePasswordWithToken_ExpiredToken_Failure() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // Токенът е изтекъл

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(resetToken);

        boolean result = userService.updatePasswordWithToken(token, newPassword);

        assertFalse(result);

        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
    }

}
