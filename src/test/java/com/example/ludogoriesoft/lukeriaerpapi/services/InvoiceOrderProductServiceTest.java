package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductConfigDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Invoice;
import com.example.ludogoriesoft.lukeriaerpapi.models.InvoiceOrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InvoiceOrderProductServiceTest {

    @Mock
    private InvoiceOrderProductRepository invoiceOrderProductRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InvoiceOrderProductService invoiceOrderProductService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateInvoiceOrderProduct_ThrowsValidationExceptionWhenOrderProductIdIsNull() {
        // Arrange
        InvoiceOrderProductDTO invoiceOrderProductDTO = new InvoiceOrderProductDTO();
        invoiceOrderProductDTO.setOrderProductId(null);

        // Act and Assert
        assertThrows(ValidationException.class, () -> invoiceOrderProductService.validateInvoiceOrderProduct(invoiceOrderProductDTO));
    }

    @Test
    void testCreateInvoiceOrderProduct_ThrowsValidationExceptionWhenOrderProductIdIsNull() {
        // Arrange
        InvoiceOrderProductDTO invoiceOrderProductDTO = new InvoiceOrderProductDTO();
        invoiceOrderProductDTO.setOrderProductId(null);

        // Act and Assert
        assertThrows(ValidationException.class, () -> invoiceOrderProductService.createInvoiceOrderProduct(invoiceOrderProductDTO));

        // Verify method calls
        verify(orderProductRepository, never()).existsById(anyLong());
        verify(invoiceRepository, never()).existsById(anyLong());
        verify(invoiceOrderProductRepository, never()).save(any());
    }
    @Test
    void testGetAllInvoiceOrderProducts() {
        // Arrange
        List<InvoiceOrderProduct> mockInvoiceOrderProducts = new ArrayList<>();
        mockInvoiceOrderProducts.add(new InvoiceOrderProduct(/* Add sample data here */));

        when(invoiceOrderProductRepository.findByDeletedFalse()).thenReturn(mockInvoiceOrderProducts);

        List<InvoiceOrderProductDTO> mockInvoiceOrderProductDTOs = new ArrayList<>();
        mockInvoiceOrderProductDTOs.add(new InvoiceOrderProductDTO(/* Add sample data here */));

        when(modelMapper.map(any(InvoiceOrderProduct.class), eq(InvoiceOrderProductDTO.class)))
                .thenReturn(mockInvoiceOrderProductDTOs.get(0));

        // Act
        List<InvoiceOrderProductDTO> result = invoiceOrderProductService.getAllInvoiceOrderProducts();

        // Assert
        assertEquals(mockInvoiceOrderProductDTOs, result);
        // You can also add more specific assertions based on your use case.
    }

    @Test
    void testGetInvoiceOrderProductById_ExistingId_ReturnsDTO() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long existingId = 1L;
        InvoiceOrderProduct mockInvoiceOrderProduct = new InvoiceOrderProduct(/* Add sample data here */);
        when(invoiceOrderProductRepository.findByIdAndDeletedFalse(existingId)).thenReturn(Optional.of(mockInvoiceOrderProduct));

        InvoiceOrderProductDTO mockInvoiceOrderProductDTO = new InvoiceOrderProductDTO(/* Add sample data here */);
        when(modelMapper.map(mockInvoiceOrderProduct, InvoiceOrderProductDTO.class))
                .thenReturn(mockInvoiceOrderProductDTO);

        // Act
        InvoiceOrderProductDTO result = invoiceOrderProductService.getInvoiceOrderProductById(existingId);

        // Assert
        Assertions.assertNotNull(result);
        assertEquals(mockInvoiceOrderProductDTO, result);
        // You can add more specific assertions based on your use case.
    }

    @Test
    void testGetInvoiceOrderProductById_NonExistingId_ThrowsNotFoundException() {
        // Arrange
        Long nonExistingId = 100L;
        when(invoiceOrderProductRepository.findByIdAndDeletedFalse(nonExistingId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> invoiceOrderProductService.getInvoiceOrderProductById(nonExistingId));
    }

    @Test
    void testDeleteInvoiceOrderProduct_ExistingId_DeletesProduct() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long existingId = 1L;
        InvoiceOrderProduct mockInvoiceOrderProduct = new InvoiceOrderProduct(/* Add sample data here */);
        when(invoiceOrderProductRepository.findByIdAndDeletedFalse(existingId)).thenReturn(Optional.of(mockInvoiceOrderProduct));

        // Act
        invoiceOrderProductService.deleteInvoiceOrderProduct(existingId);

        // Assert
        Assertions.assertTrue(mockInvoiceOrderProduct.isDeleted());
        verify(invoiceOrderProductRepository, times(1)).save(mockInvoiceOrderProduct);
        // You can add more specific assertions based on your use case.
    }

    @Test
    void testDeleteInvoiceOrderProduct_NonExistingId_ThrowsNotFoundException() {
        // Arrange
        Long nonExistingId = 100L;
        when(invoiceOrderProductRepository.findByIdAndDeletedFalse(nonExistingId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> invoiceOrderProductService.deleteInvoiceOrderProduct(nonExistingId));
        verify(invoiceOrderProductRepository, never()).save(any());
    }


    @Test
    void testValidateInvoiceOrderProduct_ValidDTO_NoExceptionsThrown() {
        // Arrange
        InvoiceOrderProductDTO validDTO = new InvoiceOrderProductDTO();
        validDTO.setOrderProductId(1L);
        validDTO.setInvoiceId(2L);

        when(orderProductRepository.existsById(validDTO.getOrderProductId())).thenReturn(true);
        when(invoiceRepository.existsById(validDTO.getInvoiceId())).thenReturn(true);

        // Act and Assert
        assertDoesNotThrow(() -> invoiceOrderProductService.validateInvoiceOrderProduct(validDTO));
    }

    @Test
    void testValidateInvoiceOrderProduct_NullOrderProductId_ThrowsValidationException() {
        // Arrange
        InvoiceOrderProductDTO invalidDTO = new InvoiceOrderProductDTO();
        invalidDTO.setInvoiceId(2L);

        // Act and Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> invoiceOrderProductService.validateInvoiceOrderProduct(invalidDTO));
        assertEquals("OrderProduct ID cannot be null!", exception.getMessage());
    }

    @Test
    void testValidateInvoiceOrderProduct_NullInvoiceId_ThrowsValidationException() {
        // Arrange
        InvoiceOrderProductDTO invalidDTO = new InvoiceOrderProductDTO();
        invalidDTO.setOrderProductId(1L);
        when(orderProductRepository.existsById(invalidDTO.getOrderProductId())).thenReturn(true);
        // Act and Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> invoiceOrderProductService.validateInvoiceOrderProduct(invalidDTO));
        assertEquals("Invoice ID cannot be null!", exception.getMessage());
    }

    @Test
    void testValidateInvoiceOrderProduct_NonExistingOrderProductId_ThrowsValidationException() {
        // Arrange
        InvoiceOrderProductDTO invalidDTO = new InvoiceOrderProductDTO();
        invalidDTO.setOrderProductId(1L);
        invalidDTO.setInvoiceId(2L);

        when(orderProductRepository.existsById(invalidDTO.getOrderProductId())).thenReturn(false);

        // Act and Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> invoiceOrderProductService.validateInvoiceOrderProduct(invalidDTO));
        assertEquals("OrderProduct does not exist with ID: 1", exception.getMessage());
    }

    @Test
    void testValidateInvoiceOrderProduct_NonExistingInvoiceId_ThrowsValidationException() {
        // Arrange
        InvoiceOrderProductDTO invalidDTO = new InvoiceOrderProductDTO();
        invalidDTO.setOrderProductId(1L);
        invalidDTO.setInvoiceId(2L);

        when(orderProductRepository.existsById(invalidDTO.getOrderProductId())).thenReturn(true);
        when(invoiceRepository.existsById(invalidDTO.getInvoiceId())).thenReturn(false);

        // Act and Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> invoiceOrderProductService.validateInvoiceOrderProduct(invalidDTO));
        assertEquals("Invoice does not exist with ID: 2", exception.getMessage());
    }

    @Test
    void testCreateInvoiceOrderProduct_ValidDTO_ReturnsDTO() {
        // Arrange
        InvoiceOrderProductDTO validDTO = new InvoiceOrderProductDTO();
        validDTO.setOrderProductId(1L);
        validDTO.setInvoiceId(1L);
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);

        when(orderProductRepository.existsById(validDTO.getOrderProductId())).thenReturn(true);
        when(invoiceRepository.existsById(validDTO.getInvoiceId())).thenReturn(true);

        InvoiceOrderProduct invoiceOrderProduct = new InvoiceOrderProduct();
        when(modelMapper.map(validDTO, InvoiceOrderProduct.class)).thenReturn(invoiceOrderProduct);
        when(invoiceOrderProductRepository.save(invoiceOrderProduct)).thenReturn(invoiceOrderProduct);

        InvoiceOrderProductDTO expectedDTO = new InvoiceOrderProductDTO();
        when(modelMapper.map(invoiceOrderProduct, InvoiceOrderProductDTO.class)).thenReturn(expectedDTO);

        InvoiceOrderProductDTO createdInvoiceOrderProductDTO = invoiceOrderProductService.createInvoiceOrderProduct(validDTO);

        verify(invoiceOrderProductRepository, times(1)).save(invoiceOrderProduct);
        assertEquals(expectedDTO, createdInvoiceOrderProductDTO);
    }

    @Test
    void testUpdateInvoiceOrderProduct_ValidInput_ReturnsDTO() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long id = 1L;
        InvoiceOrderProductDTO inputDTO = new InvoiceOrderProductDTO();
        inputDTO.setOrderProductId(1L);
        inputDTO.setInvoiceId(1L);
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        Invoice invoice2 = new Invoice();
        invoice.setId(2L);
        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct.setId(2L);

        InvoiceOrderProduct existingInvoiceOrderProduct = new InvoiceOrderProduct();
        existingInvoiceOrderProduct.setId(id);
        existingInvoiceOrderProduct.setOrderProductId(orderProduct);
        existingInvoiceOrderProduct.setInvoiceId(invoice);
        existingInvoiceOrderProduct.setDeleted(false);

        when(invoiceOrderProductRepository.findByIdAndDeletedFalse(id))
                .thenReturn(Optional.of(existingInvoiceOrderProduct));

        when(orderProductRepository.existsById(inputDTO.getOrderProductId())).thenReturn(true);
        when(invoiceRepository.existsById(inputDTO.getInvoiceId())).thenReturn(true);

        InvoiceOrderProduct updatedInvoiceOrderProduct = new InvoiceOrderProduct();
        updatedInvoiceOrderProduct.setId(id);
        updatedInvoiceOrderProduct.setOrderProductId(orderProduct2);
        updatedInvoiceOrderProduct.setInvoiceId(invoice2);
        updatedInvoiceOrderProduct.setDeleted(false);

        when(modelMapper.map(inputDTO, InvoiceOrderProduct.class)).thenReturn(updatedInvoiceOrderProduct);

        when(invoiceOrderProductRepository.save(updatedInvoiceOrderProduct))
                .thenReturn(updatedInvoiceOrderProduct);

        InvoiceOrderProductDTO expectedDTO = new InvoiceOrderProductDTO();
        expectedDTO.setId(id);
        expectedDTO.setOrderProductId(1L);
        expectedDTO.setInvoiceId(2L);
        expectedDTO.setDeleted(false);

        when(modelMapper.map(updatedInvoiceOrderProduct, InvoiceOrderProductDTO.class))
                .thenReturn(expectedDTO);

        // Act
        InvoiceOrderProductDTO result = invoiceOrderProductService.updateInvoiceOrderProduct(id, inputDTO);

        // Assert
        Assertions.assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1L, result.getOrderProductId());
        assertEquals(2L, result.getInvoiceId());
        Assertions.assertFalse(result.isDeleted());
        // You can add more specific assertions based on your use case.
    }
}
