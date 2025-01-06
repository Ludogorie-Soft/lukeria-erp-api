package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderWithProductsDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.OrderStatus;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderProductServiceTest {
    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private InvoiceOrderProductRepository invoiceOrderProductRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderProductService orderProductService;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailContentBuilder emailContentBuilder;
    @InjectMocks
    private PackageService packageService;
    @InjectMocks
    private OrderService orderService;
    @Mock
    private CartonRepository cartonRepository;
    @Mock
    private PlateRepository plateRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindInvoiceOrderProductsByInvoiceId() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        InvoiceOrderProduct invoiceOrderProduct = new InvoiceOrderProduct();
        invoiceOrderProduct.setInvoiceId(invoice);
        List<InvoiceOrderProduct> mockInvoiceOrderProductsList = new ArrayList<>();
        mockInvoiceOrderProductsList.add(invoiceOrderProduct);
        mockInvoiceOrderProductsList.add(invoiceOrderProduct);
        mockInvoiceOrderProductsList.add(invoiceOrderProduct);


        Mockito.when(invoiceOrderProductRepository.findAll()).thenReturn(mockInvoiceOrderProductsList);
        Mockito.when(orderProductService.findInvoiceOrderProductsByInvoiceId(1L)).thenReturn(mockInvoiceOrderProductsList);

        List<InvoiceOrderProduct> result = orderProductService.findInvoiceOrderProductsByInvoiceId(1L);

        assertEquals(3, result.size());
    }

    @Test
    void testReduceProducts_Failure() {
        List<InvoiceOrderProduct> invoiceOrderProductsList = new ArrayList<>();
        Mockito.when(packageRepository.findByIdAndDeletedFalse(Mockito.anyLong())).thenReturn(Optional.empty());
        boolean result = orderProductService.reduceProducts(invoiceOrderProductsList);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testReduceProducts() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        Order order = new Order();
        order.setId(1L);
        Package packageEntity = new Package();
        packageEntity.setId(1L);
        Product product = new Product();
        product.setId(1L);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        orderProduct.setNumber(20);
        orderProduct.setOrderId(order);
        orderProduct.setPackageId(packageEntity);
        InvoiceOrderProduct invoiceOrderProduct1 = new InvoiceOrderProduct();
        invoiceOrderProduct1.setOrderProductId(orderProduct);
        InvoiceOrderProduct invoiceOrderProduct2 = new InvoiceOrderProduct();
        invoiceOrderProduct2.setOrderProductId(orderProduct);
        List<InvoiceOrderProduct> invoiceOrderProductsList = Arrays.asList(invoiceOrderProduct1, invoiceOrderProduct2);

        when(productRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(product));
        boolean result = orderProductService.reduceProducts(invoiceOrderProductsList);
        assertTrue(result);
        verify(productRepository, times(2)).save(product);
        assertTrue(result);
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
    @Test
    public void testEnumValues() {
        OrderStatus[] expectedValues = {OrderStatus.ACCEPTED, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.COMPLETED};
        assertArrayEquals(expectedValues, OrderStatus.values());
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(OrderStatus.ACCEPTED, OrderStatus.valueOf("ACCEPTED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
    }

    @Test
    public void testEnumToString() {
        assertEquals("ACCEPTED", OrderStatus.ACCEPTED.toString());
        assertEquals("SHIPPED", OrderStatus.SHIPPED.toString());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.toString());
        assertEquals("COMPLETED", OrderStatus.COMPLETED.toString());
    }
    @Test
    void testGetOrderProductsOfOrders_ValidOrders() {
        // Arrange
        Order order1 = new Order();
        Order order2 = new Order();

        OrderProduct orderProduct1 = new OrderProduct();
        OrderProduct orderProduct2 = new OrderProduct();

        List<Order> orders = List.of(order1, order2);
        List<OrderProduct> orderProducts1 = List.of(orderProduct1);
        List<OrderProduct> orderProducts2 = List.of(orderProduct2);

        when(orderProductRepository.findAllByOrderId(order1)).thenReturn(orderProducts1);
        when(orderProductRepository.findAllByOrderId(order2)).thenReturn(orderProducts2);

        OrderDTO orderDTO1 = new OrderDTO();
        OrderDTO orderDTO2 = new OrderDTO();
        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        OrderProductDTO orderProductDTO2 = new OrderProductDTO();

        when(modelMapper.map(order1, OrderDTO.class)).thenReturn(orderDTO1);
        when(modelMapper.map(order2, OrderDTO.class)).thenReturn(orderDTO2);
        when(modelMapper.map(orderProduct1, OrderProductDTO.class)).thenReturn(orderProductDTO1);
        when(modelMapper.map(orderProduct2, OrderProductDTO.class)).thenReturn(orderProductDTO2);

        // Act
        List<OrderWithProductsDTO> result = orderProductService.getOrderProductsOfOrders(orders);

        // Assert
        assertEquals(2, result.size());
        assertEquals(orderDTO1, result.get(0).getOrderDTO());
        assertEquals(List.of(orderProductDTO1), result.get(0).getOrderProductDTOs());
        assertEquals(orderDTO2, result.get(1).getOrderDTO());
        assertEquals(List.of(orderProductDTO2), result.get(1).getOrderProductDTOs());
    }

    @Test
    void testGetOrderProductsOfOrders_EmptyOrdersList() {
        // Arrange
        List<Order> orders = Collections.emptyList();

        // Act
        List<OrderWithProductsDTO> result = orderProductService.getOrderProductsOfOrders(orders);

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(orderProductRepository, modelMapper);
    }

    @Test
    void testGetOrderProductsOfOrders_OrderWithoutProducts() {
        // Arrange
        Order order = new Order();
        List<Order> orders = List.of(order);

        when(orderProductRepository.findAllByOrderId(order)).thenReturn(Collections.emptyList());

        OrderDTO orderDTO = new OrderDTO();
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Act
        List<OrderWithProductsDTO> result = orderProductService.getOrderProductsOfOrders(orders);

        // Assert
        assertEquals(1, result.size());
        assertEquals(orderDTO, result.get(0).getOrderDTO());
        assertTrue(result.get(0).getOrderProductDTOs().isEmpty());
    }
    @Test
    void testGetOrderProducts_ValidOrderId() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        OrderProduct orderProduct1 = new OrderProduct();
        OrderProduct orderProduct2 = new OrderProduct();
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2);

        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        OrderProductDTO orderProductDTO2 = new OrderProductDTO();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderProductRepository.findAllByOrderId(order)).thenReturn(orderProducts);
        when(modelMapper.map(orderProduct1, OrderProductDTO.class)).thenReturn(orderProductDTO1);
        when(modelMapper.map(orderProduct2, OrderProductDTO.class)).thenReturn(orderProductDTO2);

        // Act
        List<OrderProductDTO> result = orderProductService.getOrderProducts(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(List.of(orderProductDTO1, orderProductDTO2), result);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderProductRepository, times(1)).findAllByOrderId(order);
        verify(modelMapper, times(2)).map(any(OrderProduct.class), eq(OrderProductDTO.class));
    }

    @Test
    void testGetOrderProducts_NonExistingOrderId_ThrowsNotFoundException() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderProductService.getOrderProducts(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderProductRepository, never()).findAllByOrderId(any());
        verify(modelMapper, never()).map(any(), eq(OrderProductDTO.class));
    }
}