package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    @Autowired
    private ImageService imageService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(MultipartFile file) {
        String imagePath = imageService.saveImage(file);
        return ResponseEntity.ok(imagePath);
    }

}
