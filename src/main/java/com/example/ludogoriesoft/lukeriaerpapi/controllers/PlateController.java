package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.PlateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plate")
@AllArgsConstructor
public class PlateController {
    private final PlateService plateService;

    @GetMapping
    public ResponseEntity<List<PlateDTO>> getAllPlates(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(plateService.getAllPlates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlateDTO> getPlateById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(plateService.getPlateById(id));
    }

    @PostMapping
    public ResponseEntity<PlateDTO> createPlate(@Valid @RequestBody PlateDTO plateDTO, @RequestHeader("Authorization") String auth) {
        PlateDTO cratedPlate = plateService.createPlate(plateDTO);
        return new ResponseEntity<>(cratedPlate, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlateDTO> updatePlate(@PathVariable("id") Long id, @Valid @RequestBody PlateDTO plateDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(plateService.updatePlate(id, plateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlateById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        plateService.deletePlate(id);
        return ResponseEntity.ok("Plate with id: " + id + " has been deleted successfully!");
    }
}
