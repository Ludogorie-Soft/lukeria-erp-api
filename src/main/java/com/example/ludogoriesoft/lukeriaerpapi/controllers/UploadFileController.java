package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.UploadFromFileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
@AllArgsConstructor
public class UploadFileController {
  private final UploadFromFileService uploadFromFileService;

  @PostMapping()
  public String uploadFromFile(@RequestParam("file") MultipartFile file) throws IOException {
    return uploadFromFileService.uploadFromFile(file);
  }
}
