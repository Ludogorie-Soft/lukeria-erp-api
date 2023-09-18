package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orderProduct")
@AllArgsConstructor
public class OrderProductController {
    private final OrderProductService orderProductService;
    @GetMapping
    public ResponseEntity<List<OrderProductDTO>> getAllOrderProducts() {
        return ResponseEntity.ok(orderProductService.getAllOrderProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductDTO> getOrderProductById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductService.getOrderProductById(id));
    }

    @PostMapping
    public ResponseEntity<OrderProductDTO> createOrderProduct(@Valid @RequestBody OrderProductDTO orderDTO) {
        return new ResponseEntity<>(orderProductService.createOrderProduct(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProductDTO> updateOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody OrderProductDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductService.updateOrderProduct(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderProductById(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        orderProductService.deleteOrderProduct(id);
        return ResponseEntity.ok("Order with id: " + id + " has been deleted successfully!");
    }
    @GetMapping("/lessening")
    public ResponseEntity<String> reductionQuantities(@RequestBody List<OrderProductDTO>productDTOList) throws ChangeSetPersister.NotFoundException {
        orderProductService.reductionQuantities(productDTOList);
        return ResponseEntity.ok("quantities were reduced. "+productDTOList.size()+"- of so many products");
    }

}
