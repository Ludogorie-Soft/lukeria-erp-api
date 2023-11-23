package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import com.example.ludogoriesoft.lukeriaerpapi.utils.ApiExceptionParser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiExceptionParserTest {

    @Test
    void testParseException() {
        ApiException mockedException = mock(ApiException.class);
        when(mockedException.getMessage()).thenReturn("Test Exception");
        when(mockedException.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockedException.getStatusCode()).thenReturn(500);

        ExceptionResponse result = ApiExceptionParser.parseException(mockedException);

        assertEquals(LocalDateTime.now().getYear(), result.getDateTime().getYear());
        assertEquals("Test Exception", result.getMessage());
        assertEquals(500, result.getStatusCode());
    }
}
