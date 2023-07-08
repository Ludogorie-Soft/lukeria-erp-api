package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;


public record ApiVillageNotFound(String message,
                               Throwable throwable,
                               HttpStatus httpStatus,
                               ZonedDateTime timestamp) {


}
