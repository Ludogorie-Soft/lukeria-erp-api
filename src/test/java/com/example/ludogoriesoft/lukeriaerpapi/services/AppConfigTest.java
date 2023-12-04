package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.config.AppConfig;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.InvalidTokenException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserLoginException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.AccessDeniedException;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppConfig appConfig;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void userDetailsService_UserExists_ReturnsUserDetails() {
        String username = "test@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(new User()));
        UserDetailsService userDetailsService = appConfig.userDetailsService();
        Assertions.assertNotNull(userDetailsService.loadUserByUsername(username));
    }

    @Test
    void userDetailsService_UserNotFound_ThrowsUserNotFoundException() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> appConfig.userDetailsService().loadUserByUsername(username));
    }

    @Test
    void authenticationProvider_CreatesDaoAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = appConfig.authenticationProvider();
        Assertions.assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }

    @Test
    void constructor_ShouldSetMessageAndHttpStatus() {
        String expectedMessage = "Invalid email or password";
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;
        UserLoginException userLoginException = new UserLoginException();
        Assertions.assertEquals(expectedMessage, userLoginException.getMessage());
        Assertions.assertEquals(expectedHttpStatus, userLoginException.getStatus());
    }

    @Test
    void constructorInvalidToken_ShouldSetMessageAndHttpStatus() {
        String expectedMessage = "Invalid token";
        HttpStatus expectedHttpStatus = HttpStatus.UNAUTHORIZED;
        InvalidTokenException invalidTokenException = new InvalidTokenException();
        Assertions.assertEquals(expectedMessage, invalidTokenException.getMessage());
        Assertions.assertEquals(expectedHttpStatus, invalidTokenException.getStatus());
    }

    @Test
    void constructorAccessDenied_ShouldSetMessageAndHttpStatus() {
        String expectedMessage = "Access Denied";
        HttpStatus expectedHttpStatus = HttpStatus.FORBIDDEN;
        AccessDeniedException accessDeniedException = new AccessDeniedException();
        Assertions.assertEquals(expectedMessage, accessDeniedException.getMessage());
        Assertions.assertEquals(expectedHttpStatus, accessDeniedException.getStatus());
    }

    @Test
    void defaultConstructor_ShouldSetMessageAndHttpStatus() {
        String expectedMessage = "User not found";
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;
        UserNotFoundException userNotFoundException = new UserNotFoundException();
        Assertions.assertEquals(expectedMessage, userNotFoundException.getMessage());
        Assertions.assertEquals(expectedHttpStatus, userNotFoundException.getStatus());
    }

    @Test
    void fieldConstructor_ShouldSetMessageAndHttpStatusWithField() {
        String field = "email";
        String expectedMessage = "User not found by " + field;
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;
        UserNotFoundException userNotFoundException = new UserNotFoundException(field);
        Assertions.assertEquals(expectedMessage, userNotFoundException.getMessage());
        Assertions.assertEquals(expectedHttpStatus, userNotFoundException.getStatus());
    }
}

