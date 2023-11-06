package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrder;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderRepository;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MonthlyOrderServiceTest {

    @InjectMocks
    private MonthlyOrderService monthlyOrderService;

    @Mock
    private MonthlyOrderRepository monthlyOrderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMonthlyOrders() {
        Mockito.when(monthlyOrderRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(new MonthlyOrderDTO());

        List<MonthlyOrderDTO> monthlyOrders = monthlyOrderService.getAllMonthlyOrders();
        Assertions.assertNotNull(monthlyOrders);
        Assertions.assertEquals(0, monthlyOrders.size());
    }


    @Test
    void testGetMonthlyOrderById() throws ChangeSetPersister.NotFoundException {
        Long id = 1L;
        MonthlyOrder monthlyOrder = new MonthlyOrder();
        monthlyOrder.setId(id);

        Mockito.when(monthlyOrderRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(monthlyOrder));
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(new MonthlyOrderDTO());

        MonthlyOrderDTO result = monthlyOrderService.getMonthlyOrderById(id);

        Assertions.assertNotNull(result);
    }

    @Test
    void testGetOrderById_NonExistingId() {
        when(monthlyOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> monthlyOrderService.getMonthlyOrderById(1L));

        verify(monthlyOrderRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testValidateAndCreateMonthlyOrder() {
        MonthlyOrderDTO validOrder = new MonthlyOrderDTO();
        validOrder.setClientId(1L);
        validOrder.setStartDate(LocalDate.of(2023, 1, 1));
        validOrder.setEndDate(LocalDate.of(2023, 1, 31));


        Mockito.when(clientRepository.existsById(1L)).thenReturn(true);
        Mockito.when(monthlyOrderRepository.save(Mockito.any())).thenReturn(new MonthlyOrder());

        MonthlyOrderDTO resultValid = monthlyOrderService.createMonthlyOrder(validOrder);

        Assertions.assertNull(resultValid);

        MonthlyOrderDTO invalidOrder = new MonthlyOrderDTO();
        invalidOrder.setClientId(null);


        Mockito.when(clientRepository.existsById(null)).thenReturn(false);

        assertThrows(ValidationException.class, () -> monthlyOrderService.createMonthlyOrder(invalidOrder));

        MonthlyOrderDTO invalidOrderClient = new MonthlyOrderDTO();
        invalidOrder.setClientId(1L);
        Mockito.when(clientRepository.existsById(1L)).thenReturn(false);
        assertThrows(ValidationException.class, () -> monthlyOrderService.createMonthlyOrder(invalidOrderClient));
    }

    @Test
    void testUpdateMonthlyOrderInvalid() {
        MonthlyOrderDTO validOrder = new MonthlyOrderDTO();
        validOrder.setClientId(1L);
        validOrder.setStartDate(LocalDate.of(2023, 1, 1));
        validOrder.setEndDate(LocalDate.of(2023, 1, 31));

        Mockito.when(clientRepository.existsById(1L)).thenReturn(true);
        Mockito.when(monthlyOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ChangeSetPersister.NotFoundException.class, () -> monthlyOrderService.updateMonthlyOrder(1L, validOrder));
    }

    @Test
    void testDeleteOrder_ExistingId() throws ChangeSetPersister.NotFoundException {
        MonthlyOrder monthlyOrder = new MonthlyOrder();
        monthlyOrder.setId(1L);
        monthlyOrder.setDeleted(false);
        when(monthlyOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(monthlyOrder));
        monthlyOrderService.deleteMonthlyOrder(1L);
        verify(monthlyOrderRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeleteOrder_NonExistingId() {
        when(monthlyOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> monthlyOrderService.deleteMonthlyOrder(1L));
        verify(monthlyOrderRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testFindFirstByOrderByIdDesc() {
        MonthlyOrder firstOrder = new MonthlyOrder();
        firstOrder.setId(1L);

        Mockito.when(monthlyOrderRepository.findFirstByDeletedFalseOrderByIdDesc()).thenReturn(firstOrder);
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(new MonthlyOrderDTO());

        MonthlyOrderDTO result = monthlyOrderService.findFirstByOrderByIdDesc();

        Assertions.assertNotNull(result);

    }

}
