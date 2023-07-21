package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order with id: " + id + " has been deleted successfully!");
    }
    @GetMapping("/findLastOrder")
    public ResponseEntity<OrderDTO> findFirstByOrderByIdDesc(){
        return ResponseEntity.ok(orderService.findFirstByOrderByIdDesc());
    }
}
