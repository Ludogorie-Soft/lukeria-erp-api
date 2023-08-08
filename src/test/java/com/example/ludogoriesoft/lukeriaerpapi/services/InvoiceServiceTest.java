package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import com.example.ludogoriesoft.lukeriaerpapi.repository.InvoiceRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InvoiceService invoiceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInvoices() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1L);
        invoice1.setInvoiceNumber(1L);

        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setInvoiceNumber(1L);

        List<Invoice> mockInvoices = Arrays.asList(invoice1, invoice2);
        when(invoiceRepository.findByDeletedFalse()).thenReturn(mockInvoices);

        InvoiceDTO invoiceDTO1 = new InvoiceDTO();
        invoiceDTO1.setId(1L);
        invoiceDTO1.setInvoiceNumber(1L);

        InvoiceDTO invoiceDTO2 = new InvoiceDTO();
        invoiceDTO2.setId(2L);
        invoiceDTO2.setInvoiceNumber(1L);

        when(modelMapper.map(invoice1, InvoiceDTO.class)).thenReturn(invoiceDTO1);
        when(modelMapper.map(invoice2, InvoiceDTO.class)).thenReturn(invoiceDTO2);

        List<InvoiceDTO> result = invoiceService.getAllInvoices();

        assertEquals(mockInvoices.size(), result.size());
        assertEquals(mockInvoices.get(0).getInvoiceNumber(), result.get(0).getInvoiceNumber());
        assertEquals(mockInvoices.get(1).getInvoiceNumber(), result.get(1).getInvoiceNumber());

        verify(invoiceRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockInvoices.size())).map(any(Invoice.class), eq(InvoiceDTO.class));
    }
    @Test
    void testGetInvoiceById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber(1L);

        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(invoice));

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(1L);

        when(modelMapper.map(invoice, InvoiceDTO.class)).thenReturn(invoiceDTO);

        InvoiceDTO result = invoiceService.getInvoiceById(1L);

        assertEquals(invoiceDTO.getId(), result.getId());
        assertEquals(invoiceDTO.getInvoiceNumber(), result.getInvoiceNumber());

        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(invoice, InvoiceDTO.class);
    }

    @Test
    void testGetInvoiceById_NonExistingId() {
        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> invoiceService.getInvoiceById(1L));

        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreateInvoice_InvalidInvoiceDTO_NameInvoiceNumber() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(0L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(100));
        ValidationException exception = assertThrows(ValidationException.class, () -> invoiceService.createInvoice(invoiceDTO));
        assertEquals("The invoice number must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(invoiceRepository);
    }
    @Test
    void testCreateInvoice_InvalidInvoiceDTO_SizePrice() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber(10L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(0));
        ValidationException exception = assertThrows(ValidationException.class, () -> invoiceService.createInvoice(invoiceDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(invoiceRepository);
    }

    @Test
    void testUpdateInvoice_InvalidInvoiceNumber() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(0L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(10));
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(1L);

        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingInvoice));
        assertThrows(ValidationException.class, () -> invoiceService.updateInvoice(1L, invoiceDTO));
        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }
    @Test
    void testUpdateInvoice_InvalidPrice() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(1L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(0));
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(1L);
        existingInvoice.setInvoiceNumber(1L);

        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingInvoice));
        assertThrows(ValidationException.class, () -> invoiceService.updateInvoice(1L, invoiceDTO));
        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeleteInvoice_ExistingId() throws ChangeSetPersister.NotFoundException {
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(1L);
        existingInvoice.setDeleted(false);
        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingInvoice));
        invoiceService.deleteInvoice(1L);
        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeleteInvoice_NonExistingId() {
        when(invoiceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> invoiceService.deleteInvoice(1L));
        verify(invoiceRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testUpdateInvoice_ValidInvoice() throws ChangeSetPersister.NotFoundException {
        Long invoiceId = 1L;
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(invoiceId);
        existingInvoice.setInvoiceNumber(1L);
        existingInvoice.setTotalPrice(BigDecimal.valueOf(200));

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber(1L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(200));

        when(invoiceRepository.findByIdAndDeletedFalse(invoiceId)).thenReturn(Optional.of(existingInvoice));
        when(modelMapper.map(existingInvoice, InvoiceDTO.class)).thenReturn(invoiceDTO);

        // Act
        Invoice updatedInvoice = new Invoice();
        updatedInvoice.setId(invoiceId);
        when(invoiceRepository.save(existingInvoice)).thenReturn(updatedInvoice);
        InvoiceDTO result = invoiceService.updateInvoice(invoiceId, invoiceDTO);


        verify(invoiceRepository).save(existingInvoice);
    }

    @Test
    void testCreateInvoice_ValidInvoice() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber(1L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(200));

        Invoice invoiceEntity = new Invoice();
        invoiceEntity.setInvoiceNumber(1L);
        invoiceEntity.setTotalPrice(BigDecimal.valueOf(200));

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoiceEntity);
        when(modelMapper.map(invoiceDTO, Invoice.class)).thenReturn(invoiceEntity);
        when(modelMapper.map(invoiceEntity, InvoiceDTO.class)).thenReturn(invoiceDTO);

        InvoiceDTO result = invoiceService.createInvoice(invoiceDTO);
        assertEquals(invoiceDTO.getInvoiceNumber(), result.getInvoiceNumber());
        assertEquals(invoiceDTO.getTotalPrice(), result.getTotalPrice());

        verify(invoiceRepository).save(invoiceEntity);
    }
}
