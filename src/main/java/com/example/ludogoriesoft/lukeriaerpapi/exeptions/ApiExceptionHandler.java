package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import jakarta.validation.ValidationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class ApiExceptionHandler {
    //TODO да се обедини apiException за всички класове !

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiPackageNotFound apiException = new ApiPackageNotFound(
                e.getMessage(),
                e.getCause(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
    public ResponseEntity<Object> handleApiRequestExceptionPlate(com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiPlateNotFound apiException = new ApiPlateNotFound(
                e.getMessage(),
                e.getCause(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        String errorMessage = "Not found";
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        // Handle the ValidationException here
        String errorMessage = "Validation error: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
//TODO да се обедини apiException за всички класове !
