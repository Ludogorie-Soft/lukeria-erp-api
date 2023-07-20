package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductRepository;
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

class OrderProductServiceTest {
    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderProductService orderProductService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testValidateOrderProductDTO_ValidOrder() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(1L);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(order.getId())).thenReturn(true);

        orderProductService.validateOrderProductDTO(orderDTO);
    }
    @Test
    void testCreateOrderProduct_ValidInput() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order1 = new Order();
        order1.setId(1L);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setOrderId(1L);
        orderDTO.setPackageId(1L);

        when(orderRepository.existsById(order1.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        OrderProduct order = new OrderProduct();
        when(modelMapper.map(orderDTO, OrderProduct.class)).thenReturn(order);
        when(orderProductRepository.save(order)).thenReturn(order);

        OrderProductDTO expectedDTO = new OrderProductDTO();
        when(modelMapper.map(order, OrderProductDTO.class)).thenReturn(expectedDTO);

        OrderProductDTO createdOrderDTO = orderProductService.createOrderProduct(orderDTO);

        verify(orderProductRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(orderDTO, OrderProduct.class);
        verify(modelMapper, times(1)).map(order, OrderProductDTO.class);
        assertEquals(expectedDTO, createdOrderDTO);
    }

    @Test
    void testValidateOrderProductDTO_NullOrderId() {
        OrderProductDTO orderDTO = new OrderProductDTO();

        assertThrows(ValidationException.class, () -> orderProductService.validateOrderProductDTO(orderDTO));
    }
    @Test
    void testValidateOrderProductDTO_NullProductId() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        when(orderRepository.existsById(order.getId())).thenReturn(true);

        assertThrows(ValidationException.class, () -> orderProductService.validateOrderProductDTO(orderDTO));
    }
    @Test
    void testValidateOrderProductDTO_InvalidOrder() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);

        when(orderRepository.existsById(order.getId())).thenReturn(false);

        assertThrows(ValidationException.class, () -> orderProductService.validateOrderProductDTO(orderDTO));
    }

    @Test
    void testUpdateOrderProduct_InvalidPackageId() {
        // Arrange
        OrderProductDTO orderDTO = new OrderProductDTO();
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(1L);
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(false);

        OrderProduct existingOrderProduct = new OrderProduct();
        existingOrderProduct.setId(1L);
        when(orderProductRepository.findByIdAndDeletedFalse(1L)).thenReturn(java.util.Optional.of(existingOrderProduct));

        when(packageRepository.existsById(orderDTO.getPackageId())).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> orderProductService.updateOrderProduct(1L, orderDTO));
    }

    @Test
    void testValidateOrderProductDTO_ValidPackage() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(1L);
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        orderProductService.validateOrderProductDTO(orderDTO);
    }


    @Test
    void testGetAllOrders_ReturnsListOfOrderProductDTOs() {
        OrderProduct order1 = new OrderProduct();
        order1.setId(1L);
        OrderProduct order2 = new OrderProduct();
        order2.setId(2L);
        List<OrderProduct> orders = Arrays.asList(order1, order2);

        when(orderProductRepository.findByDeletedFalse()).thenReturn(orders);
        when(modelMapper.map(any(), eq(OrderProductDTO.class))).thenReturn(new OrderProductDTO());

        List<OrderProductDTO> result = orderProductService.getAllOrderProducts();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderProductRepository, times(1)).findByDeletedFalse();
        verify(modelMapper, times(2)).map(any(), eq(OrderProductDTO.class));
    }

    @Test
    void testGetOrderById_ExistingId_ReturnsOrderProductDTO() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        OrderProduct order = new OrderProduct();
        order.setId(orderId);

        when(orderProductRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderProductDTO.class)).thenReturn(new OrderProductDTO());

        OrderProductDTO result = orderProductService.getOrderProductById(orderId);

        assertNotNull(result);

    }

    @Test
    void testGetOrderById_NonExistingId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderProductRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductService.getOrderProductById(orderId));

        verify(orderProductRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(modelMapper, never()).map(any(), eq(OrderProductDTO.class));
    }

    @Test
    void testValidateOrderProductDTO_NullOrderId_ThrowsValidationException() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        orderDTO.setOrderId(null);

        assertThrows(ValidationException.class, () -> orderProductService.validateOrderProductDTO(orderDTO));

        verify(orderRepository, never()).existsById(any());
    }

    @Test
    void testUpdateOrder_NullOrderId_ThrowsValidationException() {
        Long orderId = 1L;
        OrderProductDTO orderDTO = new OrderProductDTO();
        orderDTO.setOrderId(null);

        assertThrows(ValidationException.class, () -> orderProductService.updateOrderProduct(orderId, orderDTO));
    }

    @Test
    void testDeleteOrder_NonExistingOrderId_ThrowsNotFoundException() {
        Long orderId = 1L;

        when(orderProductRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductService.deleteOrderProduct(orderId));

        verify(orderProductRepository, times(1)).findByIdAndDeletedFalse(orderId);
        verify(orderProductRepository, never()).save(any(OrderProduct.class));
    }

    @Test
    void testCreateOrderProduct_InvalidInput() {
        OrderProductDTO orderDTO = new OrderProductDTO();
        assertThrows(ValidationException.class, () -> orderProductService.createOrderProduct(orderDTO));
    }
    @Test
    void testUpdateOrderProduct_ValidInput() throws ChangeSetPersister.NotFoundException {
        Long id = 1L;
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(1L);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        OrderProduct existingOrderProduct = new OrderProduct();
        existingOrderProduct.setId(id);
        when(orderProductRepository.findByIdAndDeletedFalse(id)).thenReturn(java.util.Optional.of(existingOrderProduct));

        OrderProduct updatedOrderProduct = new OrderProduct();
        updatedOrderProduct.setId(id);
        when(modelMapper.map(orderDTO, OrderProduct.class)).thenReturn(updatedOrderProduct);

        when(orderProductRepository.save(updatedOrderProduct)).thenReturn(updatedOrderProduct);

        OrderProductDTO expectedDTO = new OrderProductDTO();
        when(modelMapper.map(updatedOrderProduct, OrderProductDTO.class)).thenReturn(expectedDTO);

        OrderProductDTO updatedOrderDTO = orderProductService.updateOrderProduct(id, orderDTO);

        verify(orderProductRepository, times(1)).findByIdAndDeletedFalse(id);
        verify(modelMapper, times(1)).map(orderDTO, OrderProduct.class);
        verify(orderProductRepository, times(1)).save(updatedOrderProduct);
        verify(modelMapper, times(1)).map(updatedOrderProduct, OrderProductDTO.class);
        assertEquals(expectedDTO, updatedOrderDTO);
    }
    @Test
    void testUpdateOrderProduct_EntityNotFound() {
        Long id = 1L;
        OrderProductDTO orderDTO = new OrderProductDTO();
        Order order = new Order();
        order.setId(1L);
        orderDTO.setOrderId(1L);
        Package aPackage = new Package();
        aPackage.setId(1L);
        orderDTO.setPackageId(1L);

        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(packageRepository.existsById(aPackage.getId())).thenReturn(true);

        when(orderProductRepository.findByIdAndDeletedFalse(id)).thenReturn(java.util.Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductService.updateOrderProduct(id, orderDTO));
    }
    @Test
    void testDeleteOrderProduct_ValidId() throws ChangeSetPersister.NotFoundException {
        Long orderId = 1L;
        OrderProduct order = new OrderProduct();
        order.setId(orderId);
        when(orderProductRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.of(order));

        orderProductService.deleteOrderProduct(orderId);

        verify(orderProductRepository, times(1)).save(order);
        assertTrue(order.isDeleted());
    }

    @Test
    void testDeleteOrderProduct_InvalidId() {
        Long orderId = 1L;
        when(orderProductRepository.findByIdAndDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductService.deleteOrderProduct(orderId));
    }
}