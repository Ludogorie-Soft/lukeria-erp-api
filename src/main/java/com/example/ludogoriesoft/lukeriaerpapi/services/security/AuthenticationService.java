package com.example.ludogoriesoft.lukeriaerpapi.services.security;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationRequest;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RefreshTokenBodyDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RegisterRequest;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshTokenBodyDTO refreshTokenBodyDTO) throws IOException;

}
