package com.example.ludogoriesoft.lukeriaerpapi.handler;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ExceptionResponse;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserLoginException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.AccessDeniedException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.InternalServerErrorException;
import com.example.ludogoriesoft.lukeriaerpapi.utils.ApiExceptionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    // Handling RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeExceptions(RuntimeException exception) {
        exception.printStackTrace(); // Logging the exception
        return handleApiExceptions(new InternalServerErrorException());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ExceptionResponse> handleInternalAuthServiceExceptions(InternalAuthenticationServiceException exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof ApiException) {
            return handleApiExceptions((ApiException) cause);
        }

        return handleRuntimeExceptions(exception);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsExceptions() {
        return handleApiExceptions(new UserLoginException());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedExceptions() {
        return handleApiExceptions(new AccessDeniedException());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiExceptions(ApiException exception) {
        ExceptionResponse apiExceptionResponse = ApiExceptionParser.parseException(exception);

        return ResponseEntity
                .status(apiExceptionResponse.getStatus())
                .body(apiExceptionResponse);
    }
}

