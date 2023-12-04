package com.example.ludogoriesoft.lukeriaerpapi.services;
import static org.mockito.Mockito.*;

import com.example.ludogoriesoft.lukeriaerpapi.handler.LogoutHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;
@ExtendWith(MockitoExtension.class)
class LogoutHandlerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LogoutHandler logoutHandler;

    @Test
    void testLogoutWithValidAuthorizationHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        logoutHandler.logout(request, response, authentication);
        verify(tokenService, times(1)).logoutToken("mockToken");

    }
    @Test
    void testLogoutWithInvalidAuthorizationHeaderNull() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        logoutHandler.logout(request, response, authentication);
        verify(tokenService, never()).logoutToken(any());
    }
    @Test
    void testLogoutWithInvalidAuthorizationHeader() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        when(request.getHeader("Authorization")).thenReturn("null");
        logoutHandler.logout(request, response, authentication);
        verify(tokenService, never()).logoutToken(any());
    }
}
