package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.CustomerCustomPriceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customerCustomPrice")
@AllArgsConstructor
public class CustomerCustomPriceController {
    private final CustomerCustomPriceService customerCustomPriceService;

    @GetMapping
    public ResponseEntity<List<CustomerCustomPriceDTO>> getAllCustomPrices(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(customerCustomPriceService.getAllCustomPrices());
    }

    @PostMapping
    public ResponseEntity<CustomerCustomPriceDTO> createCustomPriceForCustomer(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(customerCustomPriceService.create(customerCustomPriceDTO));
    }

    @PutMapping("/")
    public ResponseEntity<CustomerCustomPriceDTO> updateCustomPrice(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(customerCustomPriceService.update(customerCustomPriceDTO));
    }

    @DeleteMapping("/")
    public ResponseEntity<CustomerCustomPriceDTO> deleteCustomPrice(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "productId") Long productId, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(customerCustomPriceService.delete(clientId, productId));
    }

    @GetMapping("/allForClient/{id}")
    public ResponseEntity<List<CustomerCustomPriceDTO>> allProductsWithAndWithoutCustomPrice(@PathVariable(name = "id") Long clientId, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(customerCustomPriceService.allProductWithCustomPriceForClient(clientId));
    }

    @GetMapping("/findByClientAndProduct")
    public ResponseEntity<CustomerCustomPriceDTO> customPriceByClientAndProduct(@RequestParam Long clientId, @RequestParam Long productId, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(customerCustomPriceService.findByClientAndProduct(clientId, productId));
    }
}
