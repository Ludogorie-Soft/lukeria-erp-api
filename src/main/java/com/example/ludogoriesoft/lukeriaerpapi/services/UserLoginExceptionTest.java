package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserLoginException;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;


class UserLoginExceptionTest {

    @Test
    public void constructor_ShouldSetMessageAndHttpStatus() {
        // Arrange
        String expectedMessage = "Invalid email or password";
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        // Act
        UserLoginException userLoginException = new UserLoginException();

        // Assert
        assertEquals(expectedMessage, userLoginException.getMessage());
        assertEquals(expectedHttpStatus, userLoginException.getStatus());
    }
}