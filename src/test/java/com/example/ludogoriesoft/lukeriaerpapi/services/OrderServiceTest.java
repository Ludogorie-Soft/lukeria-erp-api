package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderService;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderProductService;
import com.example.ludogoriesoft.lukeriaerpapi.services.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClientUserRepository clientUserRepository;

    @Mock
    private UserService userService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CustomerCustomPriceRepository customerCustomPriceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProductService orderProductService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;
    private Client client;
    private UserDTO userDTO;

    @BeforeEach
    void setupOne() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setup() {
        order = new Order();
        order.setId(1L);
        order.setOrderDate(LocalDate.now());
        order.setDeleted(false);

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setClientId(1L);

        client = new Client();
        client.setId(1L);

        userDTO = new UserDTO();
        userDTO.setId(1L);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findByDeletedFalse()).thenReturn(List.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        List<OrderDTO> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(orderDTO, orders.get(0));
    }

    @Test
    void testGetOrderById_Success() throws ChangeSetPersister.NotFoundException {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(orderDTO, result);
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void testCreateOrder_Valid() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(OrderDTO.class), eq(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO, result);
    }

    @Test
    void testCreateOrder_InvalidClient() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> orderService.createOrder(orderDTO));
        assertEquals("Client does not exist with ID: 1", exception.getMessage());
    }

    @Test
    void testUpdateOrder_Success() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(OrderDTO.class), eq(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.updateOrder(1L, orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO, result);
    }

    @Test
    void testUpdateOrder_NotFound() {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> orderService.updateOrder(1L, orderDTO));
    }

    @Test
    void testDeleteOrder_Success() throws ChangeSetPersister.NotFoundException {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).save(order);
        assertTrue(order.isDeleted());
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> orderService.deleteOrder(1L));
    }

    @Test
    void testFindFirstByOrderByIdDesc() {
        when(orderRepository.findFirstByDeletedFalseOrderByIdDesc()).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.findFirstByOrderByIdDesc();

        assertNotNull(result);
        assertEquals(orderDTO, result);
    }

    @Test
    void testCreateOrderFromShoppingCart() throws ChangeSetPersister.NotFoundException {
        ShoppingCart shoppingCart = new ShoppingCart();
        Package packageObj = new Package();
        packageObj.setId(1L);

        CartItem cartItem = new CartItem();
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(10));
        product.setPackageId(packageObj);
        cartItem.setProductId(product);
        cartItem.setQuantity(2);

        // Use a mutable list
        shoppingCart.setItems(new ArrayList<>(List.of(cartItem)));

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        when(clientUserRepository.findByUserIdAndDeletedFalse(1L)).thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client)).thenReturn(Optional.of(shoppingCart));
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(OrderDTO.class), eq(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        orderService.createOrderFromShoppingCart();

        verify(orderProductService, times(1)).createOrderProduct(any(OrderProductDTO.class));
        assertTrue(shoppingCart.getItems().isEmpty());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }
}
