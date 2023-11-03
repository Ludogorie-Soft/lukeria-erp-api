package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrder;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MonthlyOrderProductServiceTest {

    @InjectMocks
    private MonthlyOrderProductService monthlyOrderProductService;

    @Mock
    private MonthlyOrderProductRepository monthlyOrderProductRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MonthlyOrderRepository monthlyOrderRepository;
    @Mock
    private MonthlyOrder monthlyOrder;
    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllMonthlyOrderProducts() {
        Mockito.when(monthlyOrderProductRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(new MonthlyOrderDTO());

        List<MonthlyOrderProductDTO> monthlyOrders = monthlyOrderProductService.getAllMonthlyOrderProducts();
        Assertions.assertNotNull(monthlyOrders);
        Assertions.assertEquals(0, monthlyOrders.size());
    }

    @Test
    void testGetOrderById_NonExistingId() {
        when(monthlyOrderProductRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> monthlyOrderProductService.getMonthlyOrderProductById(1L));

        verify(monthlyOrderProductRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreateMonthlyOrderProduct() {
        MonthlyOrderProductDTO monthlyOrderProductDTO = new MonthlyOrderProductDTO();
        monthlyOrderProductDTO.setMonthlyOrderId(1L);
        monthlyOrderProductDTO.setPackageId(2L);
        monthlyOrderProductDTO.setOrderedQuantity(5);

        Mockito.when(monthlyOrderRepository.existsById(monthlyOrderProductDTO.getMonthlyOrderId())).thenReturn(true);
        Mockito.when(productRepository.existsById(monthlyOrderProductDTO.getPackageId())).thenReturn(true);

        MonthlyOrderProductDTO result = monthlyOrderProductService.createMonthlyOrderProduct(monthlyOrderProductDTO);

        Assertions.assertNotNull(result);

        assertDoesNotThrow(() -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProductDTO));

        MonthlyOrderProductDTO invalidMonthlyOrderProduct = new MonthlyOrderProductDTO();
        invalidMonthlyOrderProduct.setOrderedQuantity(-1);

        ValidationException validationException = Assertions.assertThrows(
                ValidationException.class,
                () -> monthlyOrderProductService.validateMonthlyOrderProduct(invalidMonthlyOrderProduct)
        );

        String expectedErrorMessage = "Ordered Quantity cannot lower than 0!";
        Assertions.assertFalse(validationException.getMessage().contains(expectedErrorMessage));
    }

    @Test
    void testDeleteOrder_ExistingId() throws ChangeSetPersister.NotFoundException {
        MonthlyOrderProduct monthlyOrder = new MonthlyOrderProduct();
        monthlyOrder.setId(1L);
        monthlyOrder.setDeleted(false);
        when(monthlyOrderProductRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(monthlyOrder));
        monthlyOrderProductService.deleteMonthlyOrderProduct(1L);
        verify(monthlyOrderProductRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeleteOrder_NonExistingId() {
        when(monthlyOrderProductRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> monthlyOrderProductService.deleteMonthlyOrderProduct(1L));
        verify(monthlyOrderProductRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testValidateMonthlyOrderProductValidData() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setMonthlyOrderId(1L);
        monthlyOrderProduct.setPackageId(2L);
        monthlyOrderProduct.setOrderedQuantity(5);
        when(monthlyOrderRepository.existsById(any())).thenReturn(true);
        when(productRepository.existsById(any())).thenReturn(true);
        assertDoesNotThrow(() -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }

    @Test
    void testValidateMonthlyOrderProductNullMonthlyOrder() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setPackageId(2L);
        monthlyOrderProduct.setOrderedQuantity(5);
        assertThrows(ValidationException.class, () -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }

    @Test
    void testValidateMonthlyOrderProductNonExistentMonthlyOrder() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setMonthlyOrderId(1L);
        monthlyOrderProduct.setPackageId(2L);
        monthlyOrderProduct.setOrderedQuantity(5);
        when(monthlyOrderRepository.existsById(any())).thenReturn(true);
        when(monthlyOrderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }

    @Test
    void testValidateMonthlyOrderProductNullProduct() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setMonthlyOrderId(1L);
        monthlyOrderProduct.setOrderedQuantity(5);
        when(monthlyOrderRepository.existsById(any())).thenReturn(true);
        assertThrows(ValidationException.class, () -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }

    @Test
    void testValidateMonthlyOrderProductNonExistentProduct() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setMonthlyOrderId(1L);
        monthlyOrderProduct.setPackageId(2L);
        monthlyOrderProduct.setOrderedQuantity(5);

        when(monthlyOrderRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(2L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }

    @Test
    void testValidateMonthlyOrderProductInvalidOrderedQuantity() {
        MonthlyOrderProductDTO monthlyOrderProduct = new MonthlyOrderProductDTO();
        monthlyOrderProduct.setMonthlyOrderId(1L);
        monthlyOrderProduct.setPackageId(2L);
        monthlyOrderProduct.setOrderedQuantity(0);

        when(monthlyOrderRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(2L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> monthlyOrderProductService.validateMonthlyOrderProduct(monthlyOrderProduct));
    }
}
