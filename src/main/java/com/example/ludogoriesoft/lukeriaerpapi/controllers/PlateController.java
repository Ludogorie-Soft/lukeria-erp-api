package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.PlateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plate")
@AllArgsConstructor
public class PlateController {
    private final PlateService plateservice;

    @GetMapping
    public ResponseEntity<List<PlateDTO>> getAllPlates() {
        return ResponseEntity.ok(plateservice.getAllPlates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlateDTO> getPlateById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(plateservice.getPlateById(id));
    }

    @PostMapping
    public ResponseEntity<PlateDTO> createPlate(@Valid @RequestBody PlateDTO PlateDTO) {
        PlateDTO cratedPlate = plateservice.createPlate(PlateDTO);
        return new ResponseEntity<>(cratedPlate, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlateDTO> updatePlate(@PathVariable("id") Long id, @Valid @RequestBody PlateDTO PlateDTO) {
        return ResponseEntity.ok(plateservice.updatePlate(id, PlateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlateById(@PathVariable("id") Long id) {
        plateservice.deletePlate(id);
        return ResponseEntity.ok("Plate with id: " + id + " has been deleted successfully!");
    }
}
