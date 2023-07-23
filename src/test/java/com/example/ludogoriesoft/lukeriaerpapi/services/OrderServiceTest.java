package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_ThrowsValidationException_WhenPlateDoesNotExist() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);
        orderDTO.setOrderDate(LocalDate.now());

        when(orderRepository.existsById(orderDTO.getClientId())).thenReturn(true);
        when(clientRepository.existsById(orderDTO.getClientId())).thenReturn(false); // Simulate non-existing plate
        assertThrows(ValidationException.class, () -> orderService.createOrder(orderDTO));
    }

    @Test
    void testCreateOrder_ValidOrderDTO_ReturnsOrderDTO() {
        // Arrange
        Client client = new Client();
        client.setId(1L);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(client.getId());

        Order order = new Order();
        order.setId(1L);
        order.setClientId(client);

        when(clientRepository.existsById(client.getId())).thenReturn(true);
        when(modelMapper.map(orderDTO, Order.class)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getClientId(), result.getClientId());

        verify(orderRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(orderDTO, Order.class);
        verify(modelMapper, times(1)).map(order, OrderDTO.class);
    }

    @Test
    void testGetAllOrders_ReturnsListOfOrderDTOs() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findByDeletedFalse()).thenReturn(orders);
        when(modelMapper.map(any(), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        List<OrderDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderRepository, times(1)).findByDeletedFalse();
        verify(modelMapper, times(2)).map(any(), eq(OrderDTO.class));
    }

    @Test
    void testGetOrderById_ExistingId_ReturnsOrderDTO() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(new OrderDTO());

        OrderDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);
    }

    @Test
    void testGetOrderById_NonExistingId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderService.getOrderById(orderId));

        verify(orderRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(modelMapper, never()).map(any(), eq(OrderDTO.class));
    }

    @Test
    void testValidateOrderDTO_ValidOrderDTO_NoExceptionThrown() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);

        when(clientRepository.existsById(orderDTO.getClientId())).thenReturn(true);

        assertDoesNotThrow(() -> orderService.validateOrderDTO(orderDTO));

        verify(clientRepository, times(1)).existsById(orderDTO.getClientId());
    }

    @Test
    void testValidateOrderDTO_NullClientId_ThrowsValidationException() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(null);

        assertThrows(ValidationException.class, () -> orderService.validateOrderDTO(orderDTO));

        verify(clientRepository, never()).existsById(any());
    }

    @Test
    void testValidateOrderDTO_NonExistingClientId_ThrowsValidationException() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);

        when(clientRepository.existsById(orderDTO.getClientId())).thenReturn(false);

        assertThrows(ValidationException.class, () -> orderService.validateOrderDTO(orderDTO));

        verify(clientRepository, times(1)).existsById(orderDTO.getClientId());
    }

    @Test
    void testUpdateOrder_NullClientId_ThrowsValidationException() {
        Long orderId = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(null);

        assertThrows(ValidationException.class, () -> orderService.updateOrder(orderId, orderDTO));
    }

    @Test
    void testDeleteOrder_ExistingOrderId_OrderDeletedSuccessfully() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.deleteOrder(orderId));

        assertTrue(order.isDeleted());

        verify(orderRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testDeleteOrder_NonExistingOrderId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderService.deleteOrder(orderId));

        verify(orderRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void testFindFirstOrderDTOByOrderByIdDesc() {
        Order order = new Order();
        order.setId(1L);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);

        when(orderRepository.findFirstByDeletedFalseOrderByIdDesc()).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.findFirstByOrderByIdDesc();

        assertEquals(orderDTO.getId(), result.getId());
    }
    @Test
    void testUpdateOrder_SetUpdatedOrderFields() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);

        when(orderRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(existingOrder));
        when(modelMapper.map(orderDTO, Order.class)).thenReturn(updatedOrder);
        when(clientRepository.existsById(1L)).thenReturn(true);

        orderService.updateOrder(orderId, orderDTO);

        assertEquals(orderId, updatedOrder.getId());
    }

}