package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/uploadImageForPackage")
    public ResponseEntity<String> uploadImageForPackage(MultipartFile file) throws IOException {
        String imagePath = imageService.saveImageForPackage(file);
        return ResponseEntity.ok(imagePath);
    }
    @PostMapping("/editImageForPackage")
    public ResponseEntity<String> editImageForPackage(MultipartFile file, Long packageId) throws IOException {
        String imagePath = imageService.editImageForPackage(file, packageId);
        return ResponseEntity.ok(imagePath);
    }
    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
        byte[] imageBytes = imageService.getImageBytes(imageName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

}
