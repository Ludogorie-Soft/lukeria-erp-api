package com.example.ludogoriesoft.lukeriaerpapi.services;


import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationRequest;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RefreshTokenBodyDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RegisterRequest;
import com.example.ludogoriesoft.lukeriaerpapi.enums.TokenType;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.InvalidTokenException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserLoginException;
import com.example.ludogoriesoft.lukeriaerpapi.models.Token;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.*;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

class AuthenticationServiceImplTest {
    @Mock
    private UserServiceImpl userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtService jwtService;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(
                userService,
                tokenService,
                jwtService,
                authenticationManager,
                modelMapper
        );
    }

    @Test
    void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest();
        User user = new User();

        when(userService.createUser(any(RegisterRequest.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockedAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("mockedRefreshToken");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        Assertions.assertNotNull(response);
        assertEquals("mockedAccessToken", response.getAccessToken());
        assertEquals("mockedRefreshToken", response.getRefreshToken());
    }

    @Test
    void testAuthenticate() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        User user = new User();

        when(userService.findByEmail(any(String.class))).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        when(jwtService.generateToken(user)).thenReturn("mockedAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("mockedRefreshToken");

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        Assertions.assertNotNull(response);
    }

    @Test
    public void testRefreshToken() throws IOException {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO("mockRefreshToken");
        User mockUser = new User();
        Token mockToken = new Token();
        mockToken.setToken("mockRefreshToken");
        mockToken.setTokenType(TokenType.REFRESH);

        when(jwtService.extractUsername(refreshTokenBodyDTO.getRefreshToken())).thenReturn("test@example.com");
        when(tokenService.findByToken("mockRefreshToken")).thenReturn(mockToken);
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);
        when(jwtService.isTokenValid("mockRefreshToken", mockUser)).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("mockAccessToken");

        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenBodyDTO);

        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());

        Mockito.verify(tokenService, times(1)).revokeAllUserTokens(mockUser);
        Mockito.verify(tokenService, times(1)).saveToken(mockUser, "mockAccessToken", TokenType.ACCESS);
        Mockito.verify(tokenService, times(1)).saveToken(mockUser, "mockRefreshToken", TokenType.REFRESH);
    }

    @Test
    public void testRefreshToken_InvalidRefreshToken() {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO(null);

        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(refreshTokenBodyDTO);
        });

        Mockito.verifyNoInteractions(jwtService, tokenService, userService);
    }

    @Test
    public void testRefreshToken_InvalidUsernameInToken() {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO("mockRefreshToken");
        when(jwtService.extractUsername(refreshTokenBodyDTO.getRefreshToken())).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(refreshTokenBodyDTO);
        });
        Mockito.verify(jwtService, times(1)).extractUsername("mockRefreshToken");
    }

    @Test
    public void testRefreshToken_InvalidTokenType() {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO("mockRefreshToken");
        User mockUser = new User();
        Token mockAccessToken = new Token();
        mockAccessToken.setToken("mockRefreshToken");
        mockAccessToken.setTokenType(TokenType.ACCESS);

        when(jwtService.extractUsername(refreshTokenBodyDTO.getRefreshToken())).thenReturn("test@example.com");
        when(tokenService.findByToken("mockRefreshToken")).thenReturn(mockAccessToken);
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(refreshTokenBodyDTO);
        });

        Mockito.verify(tokenService, times(1)).findByToken("mockRefreshToken");
    }

    @Test
    public void testRefreshToken_InvalidTokenValidation() {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO("mockRefreshToken");
        User mockUser = new User();
        Token mockToken = new Token();
        mockToken.setToken("mockRefreshToken");
        mockToken.setTokenType(TokenType.REFRESH);

        when(jwtService.extractUsername(refreshTokenBodyDTO.getRefreshToken())).thenReturn("test@example.com");
        when(tokenService.findByToken("mockRefreshToken")).thenReturn(mockToken);
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);
        when(jwtService.isTokenValid("mockRefreshToken", mockUser)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(refreshTokenBodyDTO);
        });

        Mockito.verify(jwtService, times(1)).isTokenValid("mockRefreshToken", mockUser);
        Mockito.verify(tokenService, times(1)).revokeToken(mockToken);
    }

    @Test
    void testAuthenticateWithUserNotFoundException() {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent@example.com", "password");
        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Mocked UsernameNotFoundException") {
        });
        assertThrows(UserLoginException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testRefreshTokenJwtException() {
        RefreshTokenBodyDTO refreshTokenBodyDTO = new RefreshTokenBodyDTO("mockedRefreshToken");
        when(jwtService.extractUsername(refreshTokenBodyDTO.getRefreshToken())).thenThrow(new JwtException("Mocked JwtException") {
        });
        assertThrows(InvalidTokenException.class, () -> authenticationService.refreshToken(refreshTokenBodyDTO));
    }
}
