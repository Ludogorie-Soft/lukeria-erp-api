package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.ImageService;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/uploadImageForPackage")
    public ResponseEntity<String> uploadImageForPackage(MultipartFile file) {
        String imagePath = imageService.saveImageForPackage(file);
        return ResponseEntity.ok(imagePath);
    }

    @PostMapping("/editImageForPackage")
    public ResponseEntity<String> editImageForPackage(MultipartFile file, Long packageId) {
        String imagePath = imageService.editImageForPackage(file, packageId);
        return ResponseEntity.ok(imagePath);
    }

    @PostMapping("/uploadImageForPlate")
    public ResponseEntity<String> uploadImageForPlate(MultipartFile file) {
        String imagePath = imageService.saveImageForPlate(file);
        return ResponseEntity.ok(imagePath);
    }

    @PostMapping("/editImageForPlate")
    public ResponseEntity<String> editImageForPlate(MultipartFile file, Long plateId) {
        String imagePath = imageService.editImageForPlate(file, plateId);
        return ResponseEntity.ok(imagePath);
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        byte[] imageBytes = imageService.getImageBytes(imageName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl("public, max-age=31536000");
        String eTag = "\"" + Integer.toHexString(Arrays.hashCode(imageBytes)) + "\"";
        headers.setETag(eTag);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
