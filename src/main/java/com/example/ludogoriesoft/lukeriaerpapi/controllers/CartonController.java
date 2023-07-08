package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.CartonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carton")
@AllArgsConstructor
public class CartonController {
    private final CartonService cartonService;

    @GetMapping
    public ResponseEntity<List<CartonDTO>> getAllCartons() {
        return ResponseEntity.ok(cartonService.getAllCartons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartonDTO> getCartonById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(cartonService.getCartonById(id));
    }

    @PostMapping
    public ResponseEntity<CartonDTO> createCarton(@Valid @RequestBody CartonDTO cartonDTO) {
        CartonDTO cratedCarton = cartonService.createCarton(cartonDTO);
        return new ResponseEntity<>(cratedCarton, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartonDTO> updateCarton(@PathVariable("id") Long id, @Valid @RequestBody CartonDTO cartonDTO) {
        return ResponseEntity.ok(cartonService.updateCarton(id, cartonDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartonById(@PathVariable("id") Long id) {
        cartonService.deleteCarton(id);
        return ResponseEntity.ok("Carton with id: " + id + " has been deleted successfully!");
    }
}