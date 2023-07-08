package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.PackageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/package")
@AllArgsConstructor
public class PackageController {
    private final PackageService packageService;

    @GetMapping
    public ResponseEntity<List<PackageDTO>> getAllLandscapes() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageDTO> getPackageById(@PathVariable(name = "id")Long id) {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }

    @PostMapping
    public ResponseEntity<PackageDTO> createPackage(@Valid @RequestBody PackageDTO packageDTO) {
        PackageDTO cratedPackage = packageService.createPackage(packageDTO);
        return new ResponseEntity<>(cratedPackage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackageDTO> updatePackage(@PathVariable("id") Long id, @Valid @RequestBody PackageDTO packageDTO) {
        return ResponseEntity.ok(packageService.updatePackage(id, packageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePackageById(@PathVariable("id") Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.ok("Package with id: " + id + " has been deleted successfully!!");
    }



}



