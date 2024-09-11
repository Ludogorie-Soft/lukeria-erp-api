package com.example.ludogoriesoft.lukeriaerpapi.handler;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.AccessDeniedException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    CustomExceptionHandler customExceptionHandler;

    @Autowired
    public GlobalExceptionHandler(CustomExceptionHandler customExceptionHandler) {
        this.customExceptionHandler = customExceptionHandler;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException ex) {
        ResponseEntity<ExceptionResponse> runtimeException = customExceptionHandler.handleRuntimeExceptions(ex);
        return runtimeException;
    }
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ExceptionResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        ResponseEntity<ExceptionResponse> internalAuthenticationServiceException = customExceptionHandler.handleInternalAuthServiceExceptions(ex);
        return internalAuthenticationServiceException;
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException() {
        ResponseEntity<ExceptionResponse> badCredentialsException = customExceptionHandler.handleBadCredentialsExceptions();
        return badCredentialsException;
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException() {
        ResponseEntity<ExceptionResponse> accessDeniedException = customExceptionHandler.handleAccessDeniedExceptions();
        return accessDeniedException;
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException(ApiException ex) {
        ResponseEntity<ExceptionResponse> apiException = customExceptionHandler.handleApiExceptions(ex);
        return apiException;
    }
}
