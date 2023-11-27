package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import com.example.ludogoriesoft.lukeriaerpapi.utils.ObjectMapperHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObjectMapperHelperTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApiException exception;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ServletOutputStream outputStream;

    @InjectMocks
    private ObjectMapperHelper objectMapperHelper;

    @Test
    void testWriteExceptionToObjectMapper() throws IOException {
        MockitoAnnotations.openMocks(this);
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        when(httpServletResponse.getOutputStream()).thenReturn(outputStream);
        when(httpServletResponse.getStatus()).thenReturn(500);
        when(httpServletResponse.getOutputStream()).thenReturn(outputStream);
        when(httpServletResponse.getStatus()).thenReturn(500);
        doNothing().when(objectMapper).writeValue(any(ServletOutputStream.class), eq(exceptionResponse));
        ObjectMapperHelper.writeExceptionToObjectMapper(objectMapper, exception, httpServletResponse);

        verify(httpServletResponse).setContentType("application/json");
    }
}
