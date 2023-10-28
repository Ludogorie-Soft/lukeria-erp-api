package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException() {
        super("Invalid token", HttpStatus.UNAUTHORIZED);
    }
}
