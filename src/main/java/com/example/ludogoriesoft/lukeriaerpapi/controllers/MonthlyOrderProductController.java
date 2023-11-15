package com.example.ludogoriesoft.lukeriaerpapi.controllers;


import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.MonthlyOrderProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monthlyOrderProduct")
@AllArgsConstructor
public class MonthlyOrderProductController {
    private final MonthlyOrderProductService monthlyOrderProductService;

    @GetMapping
    public ResponseEntity<List<MonthlyOrderProductDTO>> getAllMonthlyProductOrders(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(monthlyOrderProductService.getAllMonthlyOrderProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyOrderProductDTO> getMonthlyOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(monthlyOrderProductService.getMonthlyOrderProductById(id));
    }

    @PostMapping
    public ResponseEntity<MonthlyOrderProductDTO> createMonthlyProductOrder(@Valid @RequestBody MonthlyOrderProductDTO monthlyOrderProduct, @RequestHeader("Authorization") String auth) {
        return new ResponseEntity<>(monthlyOrderProductService.createMonthlyOrderProduct(monthlyOrderProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonthlyOrderProductDTO> updateMonthlyProductOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderProductDTO monthlyOrderDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(monthlyOrderProductService.updateMonthlyOrderProduct(id, monthlyOrderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMonthlyProductOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        monthlyOrderProductService.deleteMonthlyOrderProduct(id);
        return ResponseEntity.ok("Monthly order with id: " + id + " has been deleted successfully!");
    }

}
