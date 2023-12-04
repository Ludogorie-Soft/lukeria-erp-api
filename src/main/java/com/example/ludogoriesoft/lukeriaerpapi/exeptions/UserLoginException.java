package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import org.springframework.http.HttpStatus;

public class UserLoginException extends ApiException {
    public UserLoginException() {
        super("Invalid email or password", HttpStatus.BAD_REQUEST);
    }
}