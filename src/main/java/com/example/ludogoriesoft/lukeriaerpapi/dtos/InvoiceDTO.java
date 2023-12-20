package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private LocalDate invoiceDate;
    @Min(value = 1, message = "Номера на фактурата трябда да е по-голяма от 0!")
    private Long invoiceNumber;
    @Min(value = 1, message = "Цената трябва да бъде по-голяма от 0!")
    private BigDecimal totalPrice;
    private boolean isCashPayment;
    private LocalDate deadline;
    private boolean created;
    private String bankAccount;
}
