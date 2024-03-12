package com.example.ludogoriesoft.lukeriaerpapi.exeptions;

import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.ApiException;
import org.springframework.http.HttpStatus;

public class PackageNotFoundException extends ApiException {
    public PackageNotFoundException(Long packageId) {
        super("Package with id : " + packageId + " not found", HttpStatus.NOT_FOUND);
    }
}
