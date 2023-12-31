package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductConfigDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.InvoiceOrderProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoiceOrderProduct")
@AllArgsConstructor
public class InvoiceOrderProductController {
    private final InvoiceOrderProductService invoiceOrderProductService;

    @GetMapping
    public ResponseEntity<List<InvoiceOrderProductDTO>> getAllInvoiceOrderProduct(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(invoiceOrderProductService.getAllInvoiceOrderProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceOrderProductDTO> getInvoiceOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(invoiceOrderProductService.getInvoiceOrderProductById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceOrderProductDTO> createInvoiceOrderProduct(@Valid @RequestBody InvoiceOrderProductDTO invoiceOrderProductDTO, @RequestHeader("Authorization") String auth) {
        invoiceOrderProductService.validateInvoiceOrderProduct(invoiceOrderProductDTO);
        return new ResponseEntity<>(invoiceOrderProductService.createInvoiceOrderProduct(invoiceOrderProductDTO), HttpStatus.CREATED);
    }

    @PostMapping("/withIds")
    public ResponseEntity<String> createInvoiceOrderProductWithIdsList(@RequestBody InvoiceOrderProductConfigDTO configDTO, @RequestHeader("Authorization") String auth) {
        return new ResponseEntity<>(invoiceOrderProductService.createInvoiceOrderProductWithIds(configDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceOrderProductDTO> updateInvoiceOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody InvoiceOrderProductDTO invoiceOrderProductDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(invoiceOrderProductService.updateInvoiceOrderProduct(id, invoiceOrderProductDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoiceOrderProductById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        invoiceOrderProductService.deleteInvoiceOrderProduct(id);
        return ResponseEntity.ok("InvoiceOrderProduct with id: " + id + " has been deleted successfully!");
    }

}
