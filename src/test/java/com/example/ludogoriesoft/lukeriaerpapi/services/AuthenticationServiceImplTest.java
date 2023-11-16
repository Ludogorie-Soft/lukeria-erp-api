package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.*;
import com.example.ludogoriesoft.lukeriaerpapi.enums.TokenType;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.InvalidTokenException;
import com.example.ludogoriesoft.lukeriaerpapi.models.Token;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.*;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

class AuthenticationServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserServiceAuthentication userServiceAuthentication;

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
                userServiceAuthentication,
                tokenService,
                jwtService,
                authenticationManager,
                modelMapper
        );
    }

    @Test
    void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest();
        UserDTO userDTO = new UserDTO();
        User user = new User();
        when(userService.createUser(any(User.class))).thenReturn(userDTO);
        when(jwtService.generateToken(user)).thenReturn("mockedAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("mockedRefreshToken");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        Assertions.assertNotNull(response);
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
        Mockito.verify(jwtService, Mockito.times(1)).extractUsername("mockRefreshToken");
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

        Mockito.verify(tokenService, Mockito.times(1)).findByToken("mockRefreshToken");
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
    }

    @Test
    public void testMe_InvalidAccessToken() {
        AccessTokenBodyDTO accessTokenBodyDTO = new AccessTokenBodyDTO("invalidAccessToken");

        when(tokenService.findByToken("invalidAccessToken")).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.me(accessTokenBodyDTO);
        });

        Mockito.verifyNoInteractions(jwtService, modelMapper);
    }

    @Test
    public void testMe_InvalidTokenValidation() {
        // Arrange
        AccessTokenBodyDTO accessTokenBodyDTO = new AccessTokenBodyDTO("mockAccessToken");
        User mockUser = new User(); // create a mock User object
        Token mockAccessToken = new Token();
        Token mockRefreshToken = new Token();

        mockAccessToken.setToken("mockAccessToken");
        mockAccessToken.setUser(mockUser);

        mockRefreshToken.setToken("mockRefreshToken");
        mockRefreshToken.setTokenType(TokenType.REFRESH);

        when(tokenService.findByToken("mockAccessToken")).thenReturn(mockAccessToken);
        when(tokenService.findByUser(mockUser)).thenReturn(List.of(mockAccessToken, mockRefreshToken));
        when(jwtService.isTokenValid("mockAccessToken", mockUser)).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.me(accessTokenBodyDTO);
        });

        Mockito.verify(jwtService, Mockito.times(1)).isTokenValid("mockAccessToken", mockUser);
        Mockito.verify(tokenService, Mockito.times(1)).revokeAllUserTokens(mockUser);
    }
}

