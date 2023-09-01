package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import com.example.ludogoriesoft.lukeriaerpapi.repository.InvoiceRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper modelMapper;
    public static final Long FIRST_INVOICE_NUMBER = 2000000000L;

    public Long findLastInvoiceNumberStartingWithTwo()  {
        String prefix = "2";

        List<String> lastInvoiceNumbers = invoiceRepository.findLastInvoiceNumberStartingWith(prefix);

        if (lastInvoiceNumbers.isEmpty()) {
           return FIRST_INVOICE_NUMBER;
        }

        String maxLastDigitNumber = lastInvoiceNumbers.stream()
                .max(Comparator.comparing(this::getLastDigit))
                .orElse("0");

        return Long.parseLong(maxLastDigitNumber);
    }

    private int getLastDigit(String invoiceNumber) {
        return Character.getNumericValue(invoiceNumber.charAt(invoiceNumber.length() - 1));
    }
    public List<InvoiceDTO> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findByDeletedFalse();
        return invoices.stream().map(invoice -> modelMapper.map(invoice, InvoiceDTO.class)).toList();
    }

    public InvoiceDTO getInvoiceById(Long id) throws ChangeSetPersister.NotFoundException {
        Invoice invoice = invoiceRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(invoice, InvoiceDTO.class);
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        if (invoiceDTO.getInvoiceNumber() <= 0) {
            throw new ValidationException("The invoice number must be greater than zero");
        }
        if (invoiceDTO.getTotalPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        invoiceDTO.setCreated(true);
        Invoice invoiceEntity = invoiceRepository.save(modelMapper.map(invoiceDTO, Invoice.class));
        return modelMapper.map(invoiceEntity, InvoiceDTO.class);
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) throws ChangeSetPersister.NotFoundException {
        Invoice existingInvoice = invoiceRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (invoiceDTO.getInvoiceNumber() <= 0) {
            throw new ValidationException("The invoice number must be greater than zero");
        }
        if (invoiceDTO.getTotalPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        existingInvoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        existingInvoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        existingInvoice.setTotalPrice(invoiceDTO.getTotalPrice());
        existingInvoice.setCashPayment(invoiceDTO.isCashPayment());
        existingInvoice.setDeadline(invoiceDTO.getDeadline());
        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        updatedInvoice.setId(id);
        return modelMapper.map(updatedInvoice, InvoiceDTO.class);
    }

    public void deleteInvoice(Long id) throws ChangeSetPersister.NotFoundException {
        Invoice invoice = invoiceRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
    }
}
