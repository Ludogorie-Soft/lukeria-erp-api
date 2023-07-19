package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProductClient;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class OrderProductClientServiceTest {
    @Mock
    private OrderProductClientRepository orderProductClientRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderProductClientService orderProductClientService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testValidateOrderProductClientDTO_ValidOrder() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(aPackage);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(order.getId())).thenReturn(true);

        orderProductClientService.validateOrderProductClientDTO(orderDTO);
    }
    @Test
    public void testCreateOrderProductClient_ValidInput() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order1 = new Order();
        order1.setId(1L);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setOrderId(order1);
        orderDTO.setPackageId(aPackage);

        when(orderRepository.existsById(order1.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        OrderProductClient order = new OrderProductClient();
        when(modelMapper.map(orderDTO, OrderProductClient.class)).thenReturn(order);
        when(orderProductClientRepository.save(order)).thenReturn(order);

        OrderProductClientDTO expectedDTO = new OrderProductClientDTO();
        when(modelMapper.map(order, OrderProductClientDTO.class)).thenReturn(expectedDTO);

        OrderProductClientDTO createdOrderDTO = orderProductClientService.createOrderProductClient(orderDTO);

        verify(orderProductClientRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(orderDTO, OrderProductClient.class);
        verify(modelMapper, times(1)).map(order, OrderProductClientDTO.class);
        assertEquals(expectedDTO, createdOrderDTO);
    }

    @Test
    public void testValidateOrderProductClientDTO_NullOrderId() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();

        assertThrows(ValidationException.class, () -> orderProductClientService.validateOrderProductClientDTO(orderDTO));
    }
    @Test
    public void testValidateOrderProductClientDTO_NullProductId() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        when(orderRepository.existsById(order.getId())).thenReturn(true);

        assertThrows(ValidationException.class, () -> orderProductClientService.validateOrderProductClientDTO(orderDTO));
    }
    @Test
    public void testValidateOrderProductClientDTO_InvalidOrder() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);

        when(orderRepository.existsById(order.getId())).thenReturn(false);

        assertThrows(ValidationException.class, () -> orderProductClientService.validateOrderProductClientDTO(orderDTO));
    }

    @Test
    public void testUpdateOrderProductClient_InvalidPackageId() {
        // Arrange
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(aPackage);
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(false);

        OrderProductClient existingOrderProductClient = new OrderProductClient();
        existingOrderProductClient.setId(1L);
        when(orderProductClientRepository.findByIdAndDeletedFalse(1L)).thenReturn(java.util.Optional.of(existingOrderProductClient));

        when(packageRepository.existsById(orderDTO.getPackageId().getId())).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> orderProductClientService.updateOrderProductClient(1L, orderDTO));
    }

    @Test
    public void testValidateOrderProductClientDTO_ValidPackage() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(aPackage);
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        orderProductClientService.validateOrderProductClientDTO(orderDTO);
    }


    @Test
    void testGetAllOrders_ReturnsListOfOrderProductClientDTOs() {
        OrderProductClient order1 = new OrderProductClient();
        order1.setId(1L);
        OrderProductClient order2 = new OrderProductClient();
        order2.setId(2L);
        List<OrderProductClient> orders = Arrays.asList(order1, order2);

        when(orderProductClientRepository.findByDeletedFalse()).thenReturn(orders);
        when(modelMapper.map(any(), eq(OrderProductClientDTO.class))).thenReturn(new OrderProductClientDTO());

        List<OrderProductClientDTO> result = orderProductClientService.getAllOrderProductClients();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderProductClientRepository, times(1)).findByDeletedFalse();
        verify(modelMapper, times(2)).map(any(), eq(OrderProductClientDTO.class));
    }

    @Test
    void testGetOrderById_ExistingId_ReturnsOrderProductClientDTO() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        OrderProductClient order = new OrderProductClient();
        order.setId(orderId);

        when(orderProductClientRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderProductClientDTO.class)).thenReturn(new OrderProductClientDTO());

        OrderProductClientDTO result = orderProductClientService.getOrderProductClientById(orderId);

        assertNotNull(result);

    }

    @Test
    void testGetOrderById_NonExistingId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderProductClientRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductClientService.getOrderProductClientById(orderId));

        verify(orderProductClientRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(modelMapper, never()).map(any(), eq(OrderProductClientDTO.class));
    }

    @Test
    void testValidateOrderProductClientDTO_NullOrderId_ThrowsValidationException() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        orderDTO.setOrderId(null);

        assertThrows(ValidationException.class, () -> orderProductClientService.validateOrderProductClientDTO(orderDTO));

        verify(orderRepository, never()).existsById(any());
    }

    @Test
    void testUpdateOrder_NullOrderId_ThrowsValidationException() {
        Long orderId = 1L;
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        orderDTO.setOrderId(null);

        assertThrows(ValidationException.class, () -> orderProductClientService.updateOrderProductClient(orderId, orderDTO));
    }

    @Test
    void testDeleteOrder_NonExistingOrderId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderProductClientRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductClientService.deleteOrderProductClient(orderId));

        verify(orderProductClientRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(orderProductClientRepository, never()).save(any(OrderProductClient.class));
    }

    @Test
    public void testCreateOrderProductClient_InvalidInput() {
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        assertThrows(ValidationException.class, () -> orderProductClientService.createOrderProductClient(orderDTO));
    }
    @Test
    public void testUpdateOrderProductClient_ValidInput() throws ChangeSetPersister.NotFoundException {
        Long id = 1L;
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(aPackage);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        OrderProductClient existingOrderProductClient = new OrderProductClient();
        existingOrderProductClient.setId(id);
        when(orderProductClientRepository.findByIdAndDeletedFalse(id)).thenReturn(java.util.Optional.of(existingOrderProductClient));

        OrderProductClient updatedOrderProductClient = new OrderProductClient();
        updatedOrderProductClient.setId(id);
        when(modelMapper.map(orderDTO, OrderProductClient.class)).thenReturn(updatedOrderProductClient);

        when(orderProductClientRepository.save(updatedOrderProductClient)).thenReturn(updatedOrderProductClient);

        OrderProductClientDTO expectedDTO = new OrderProductClientDTO();
        when(modelMapper.map(updatedOrderProductClient, OrderProductClientDTO.class)).thenReturn(expectedDTO);

        OrderProductClientDTO updatedOrderDTO = orderProductClientService.updateOrderProductClient(id, orderDTO);

        verify(orderProductClientRepository, times(1)).findByIdAndDeletedFalse(id);
        verify(modelMapper, times(1)).map(orderDTO, OrderProductClient.class);
        verify(orderProductClientRepository, times(1)).save(updatedOrderProductClient);
        verify(modelMapper, times(1)).map(updatedOrderProductClient, OrderProductClientDTO.class);
        assertEquals(expectedDTO, updatedOrderDTO);
    }
    @Test
    public void testUpdateOrderProductClient_EntityNotFound() {
        Long id = 1L;
        OrderProductClientDTO orderDTO = new OrderProductClientDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(order);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(aPackage);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        when(orderProductClientRepository.findByIdAndDeletedFalse(id)).thenReturn(java.util.Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductClientService.updateOrderProductClient(id, orderDTO));
    }
    @Test
    public void testDeleteOrderProductClient_ValidId() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        OrderProductClient order = new OrderProductClient();
        order.setId(orderId);
        when(orderProductClientRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));

        orderProductClientService.deleteOrderProductClient(orderId);

        verify(orderProductClientRepository, times(1)).save(order);
        assertTrue(order.isDeleted());
    }

    @Test
    public void testDeleteOrderProductClient_InvalidId() {
        Long orderId = 1L;
        when(orderProductClientRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductClientService.deleteOrderProductClient(orderId));
    }
}