package com.example.ludogoriesoft.lukeriaerpapi.utils;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;

import java.time.LocalDateTime;

public class ApiExceptionParser {
    public static ExceptionResponse parseException(ApiException exception) {
        return ExceptionResponse
                .builder()
                .dateTime(LocalDateTime.now())
                .message(exception.getMessage())
                .status(exception.getStatus())
                .statusCode(exception.getStatusCode())
                .build();
    }
}
