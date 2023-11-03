package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@AllArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable(name = "id") Long id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO cratedInvoice = invoiceService.createInvoice(invoiceDTO);
        return new ResponseEntity<>(cratedInvoice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable("id") Long id, @Valid @RequestBody InvoiceDTO invoiceDTO) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoiceById(@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice with id: " + id + " has been deleted successfully!");
    }

    @GetMapping("/number")
    public ResponseEntity<Long> findLastInvoiceNumberStartingWith() {
        return ResponseEntity.ok(invoiceService.findLastInvoiceNumberStartingWithTwo());
    }

    @GetMapping("/number/abroad")
    public ResponseEntity<Long> findLastInvoiceNumberStartingWithOne() {
        return ResponseEntity.ok(invoiceService.findLastInvoiceNumberStartingWithOne());
    }
}