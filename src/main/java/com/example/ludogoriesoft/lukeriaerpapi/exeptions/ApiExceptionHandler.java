package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import jakarta.validation.ValidationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService.publishMessage;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        String errorMessage = "Not found!";
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        String errorMessage = "Validation error: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public void alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex) {
        publishMessage("lukeria-notifications","Error occurred from the BACKEND application ->" + ex.getMessage());
    }
}