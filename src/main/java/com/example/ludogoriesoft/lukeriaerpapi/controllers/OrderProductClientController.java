package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderProductClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orderProductClient")
@AllArgsConstructor
public class OrderProductClientController {
    private final OrderProductClientService orderProductClientService;
    @GetMapping
    public ResponseEntity<List<OrderProductClientDTO>> getAllOrders() {
        return ResponseEntity.ok(orderProductClientService.getAllOrderProductClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductClientDTO> getOrderById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductClientService.getOrderProductClientById(id));
    }

    @PostMapping
    public ResponseEntity<OrderProductClientDTO> createOrder(@Valid @RequestBody OrderProductClientDTO orderDTO) {
        return new ResponseEntity<>(orderProductClientService.createOrderProductClient(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProductClientDTO> updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderProductClientDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductClientService.updateOrderProductClient(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        orderProductClientService.deleteOrderProductClient(id);
        return ResponseEntity.ok("Order with id: " + id + " has been deleted successfully!");
    }

}
