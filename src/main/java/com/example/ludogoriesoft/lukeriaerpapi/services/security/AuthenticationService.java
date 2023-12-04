package com.example.ludogoriesoft.lukeriaerpapi.services.security;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.*;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshTokenBodyDTO refreshTokenBodyDTO) throws IOException;

}
