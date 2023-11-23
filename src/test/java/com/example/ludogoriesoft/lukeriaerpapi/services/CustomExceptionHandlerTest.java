package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.InternalServerErrorException;
import com.example.ludogoriesoft.lukeriaerpapi.handler.CustomExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}