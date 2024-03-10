package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import org.springframework.http.HttpStatus;

public class PlateNotFoundException  extends ApiException {
    public PlateNotFoundException(Long plateId) {
        super("Plate with id : " + plateId + " not found", HttpStatus.NOT_FOUND);
    }
}
