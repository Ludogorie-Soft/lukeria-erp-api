package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrder;
import com.example.ludogoriesoft.lukeriaerpapi.services.MonthlyOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monthlyOrder")
@AllArgsConstructor
public class MonthlyOrderController {
    private final MonthlyOrderService monthlyOrderService;

    @GetMapping
    public ResponseEntity<List<MonthlyOrderDTO>> getAllMonthlyOrders() {
        return ResponseEntity.ok(monthlyOrderService.getAllMonthlyOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyOrderDTO> getMonthlyOrderById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(monthlyOrderService.getMonthlyOrderById(id));
    }

    @PostMapping
    public ResponseEntity<MonthlyOrderDTO> createMonthlyOrder(@Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO) {
        return new ResponseEntity<>(monthlyOrderService.createMonthlyOrder(monthlyOrderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonthlyOrderDTO> updateMonthlyOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(monthlyOrderService.updateMonthlyOrder(id, monthlyOrderDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMonthlyOrder(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        monthlyOrderService.deleteMonthlyOrder(id);
        return ResponseEntity.ok("Monthly order with id: " + id + " has been deleted successfully!");
    }
    @GetMapping("/findLastMonthlyOrder")
    public ResponseEntity<MonthlyOrderDTO> findFirstByOrderByIdDesc(){
        return ResponseEntity.ok(monthlyOrderService.findFirstByOrderByIdDesc());
    }
}
