package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.PackageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    public ResponseEntity<List<PackageDTO>> getAllPackages(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageDTO> getPackageById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }
    @GetMapping("/materials/{id}")
    public void getAllMaterialsForPackageById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        packageService.sendProductStockReportById(id);
    }

    @PostMapping
    public ResponseEntity<PackageDTO> createPackage(@Valid @RequestBody PackageDTO packageDTO, @RequestHeader("Authorization") String auth) {
        PackageDTO cratedPackage = packageService.createPackage(packageDTO);
        return new ResponseEntity<>(cratedPackage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackageDTO> updatePackage(@PathVariable("id") Long id, @Valid @RequestBody PackageDTO packageDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(packageService.updatePackage(id, packageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePackageById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        packageService.deletePackage(id);
        return ResponseEntity.ok("Package with id: " + id + " has been deleted successfully!");
    }
}
