package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

public class CustomEmailException extends RuntimeException {
    public CustomEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
