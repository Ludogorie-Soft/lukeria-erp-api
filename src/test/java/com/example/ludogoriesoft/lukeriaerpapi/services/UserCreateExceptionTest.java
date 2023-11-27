package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserCreateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

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

    @Test
    void testUserCreateExceptionWithValidationErrors() {
        Set<ConstraintViolation<?>> validationErrors = new HashSet<>();
        validationErrors.add(new MockConstraintViolation("Error 1"));
        validationErrors.add(new MockConstraintViolation("Error 2"));
        UserCreateException exception = new UserCreateException(validationErrors);
        assertEquals("Error 1\nError 2", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    private static class MockConstraintViolation implements ConstraintViolation<Object> {
        private final String message;

        public MockConstraintViolation(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> aClass) {
            return null;
        }
    }

}
