package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.UploadFromFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
@AllArgsConstructor
public class UploadFileController {
    private final UploadFromFileService uploadFromFileService;

    @PostMapping()
    public ResponseEntity<String> uploadFromFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String auth) throws IOException {
        return uploadFromFileService.packageUploadStatus(file);
    }
}
