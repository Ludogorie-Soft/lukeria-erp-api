package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserCreateException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCreateExceptionTest {

    @Test
    void testConstructor_WithUniqueFlagTrue_ShouldCreateException() {
        boolean isUnique = true;
        UserCreateException userCreateException = new UserCreateException(isUnique);
        assertEquals("User with the same email already exists", userCreateException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, userCreateException.getStatus());
    }

    @Test
    void testConstructor_WithUniqueFlagFalse_ShouldCreateException() {
        boolean isUnique = false;
        UserCreateException userCreateException = new UserCreateException(isUnique);
        assertEquals("Invalid user data", userCreateException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, userCreateException.getStatus());
    }

    @Test
    void testUserCreateExceptionWithUniqueFlag() {
        boolean isUnique = true;
        UserCreateException exception = new UserCreateException(isUnique);
        assertEquals("User with the same email already exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
