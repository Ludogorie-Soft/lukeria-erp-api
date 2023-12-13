package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.InternalServerErrorException;
import com.example.ludogoriesoft.lukeriaerpapi.handler.CustomExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomExceptionHandlerTest {

    @Test
    void testHandleRuntimeExceptions() {
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();
        InternalServerErrorException mockException = mock(InternalServerErrorException.class);
        when(mockException.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<ExceptionResponse> responseEntity = customExceptionHandler.handleRuntimeExceptions(mockException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(mockException, times(1)).printStackTrace();
    }

    @Test
    void handleInternalAuthServiceExceptions() {
        CustomExceptionHandler handler = new CustomExceptionHandler();

        InternalAuthenticationServiceException exception = new InternalAuthenticationServiceException("Test exception");

        ResponseEntity<ExceptionResponse> response = handler.handleInternalAuthServiceExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleBadCredentialsExceptions() {
        CustomExceptionHandler handler = new CustomExceptionHandler();
        ResponseEntity<ExceptionResponse> response = handler.handleBadCredentialsExceptions();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleAccessDeniedExceptions() {
        CustomExceptionHandler handler = new CustomExceptionHandler();
        ResponseEntity<ExceptionResponse> response = handler.handleAccessDeniedExceptions();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}