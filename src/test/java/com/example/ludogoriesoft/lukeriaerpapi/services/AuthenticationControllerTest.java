package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationRequest;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RefreshTokenBodyDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RegisterRequest;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private LogoutHandler logoutHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(/* provide necessary details */);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(/* provide necessary details */);
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authenticationResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(asJsonString(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testAuthenticate() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .content(asJsonString(authenticationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshTokenBodyDTO refreshTokenBody = new RefreshTokenBodyDTO();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        when(authenticationService.refreshToken(any(RefreshTokenBodyDTO.class))).thenReturn(authenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
                        .content(asJsonString(refreshTokenBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    private String asJsonString(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}