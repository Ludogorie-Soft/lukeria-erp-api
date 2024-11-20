package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ShoppingCartDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CartItem;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.models.ShoppingCart;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartItemRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientUserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ShoppingCartRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ClientUserRepository clientUserRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CustomerCustomPriceRepository customerCustomPriceRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart_ProductAlreadyInCart_WithinAvailableQuantity() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        int quantityToAdd = 2;

        UserDTO user = new UserDTO();
        user.setId(1L);

        Client client = new Client();
        client.setId(1L);

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setClientId(client);
        List<CartItem> cartItems = new ArrayList<>();
        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product);
        cartItem.setQuantity(3);
        cartItems.add(cartItem);

        shoppingCart.setItems(cartItems);

        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(clientUserRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client)).thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        shoppingCartService.addToCart(productId, quantityToAdd);

        // Assert
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository, never()).save(any());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void testAddToCart_ProductAlreadyInCart_ExceedsAvailableQuantity() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        int quantityToAdd = 8;

        UserDTO user = new UserDTO();
        user.setId(1L);

        Client client = new Client();
        client.setId(1L);

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setClientId(client);
        List<CartItem> cartItems = new ArrayList<>();
        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product);
        cartItem.setQuantity(3);
        cartItems.add(cartItem);

        shoppingCart.setItems(cartItems);

        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(clientUserRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client)).thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> shoppingCartService.addToCart(productId, quantityToAdd)
        );
        assertEquals("There is no that much quantity", exception.getMessage());
    }

    @Test
    void testAddToCart_ProductNotInCart_WithinAvailableQuantity() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        int quantityToAdd = 5;

        UserDTO user = new UserDTO();
        user.setId(1L);

        Client client = new Client();
        client.setId(1L);

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setClientId(client);
        shoppingCart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);
        product.setPrice(BigDecimal.valueOf(100.0));

        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(clientUserRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client)).thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        shoppingCartService.addToCart(productId, quantityToAdd);

        // Assert
        assertEquals(1, shoppingCart.getItems().size());
        CartItem newCartItem = shoppingCart.getItems().get(0);
        assertEquals(product, newCartItem.getProductId());
        assertEquals(quantityToAdd, newCartItem.getQuantity());
        assertEquals(BigDecimal.valueOf(100.0), newCartItem.getPrice());
        verify(cartItemRepository, times(1)).save(newCartItem);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void testAddToCart_ProductNotInCart_ExceedsAvailableQuantity() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        int quantityToAdd = 15;

        UserDTO user = new UserDTO();
        user.setId(1L);

        Client client = new Client();
        client.setId(1L);

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setClientId(client);
        shoppingCart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);

        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(clientUserRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client)).thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> shoppingCartService.addToCart(productId, quantityToAdd)
        );
        assertEquals("There is no that much quantity", exception.getMessage());
    }

    @Test
    void testAddToCart_ValidProduct_AddsToCart() throws Exception {
        Long productId = 1L;
        int quantity = 2;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Client client = new Client();
        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);
        product.setPrice(BigDecimal.valueOf(100.0));

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        when(clientUserRepository.findByUserIdAndDeletedFalse(userDTO.getId()))
                .thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client))
                .thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        shoppingCartService.addToCart(productId, quantity);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void testAddToCart_InsufficientStock_ThrowsValidationException() {
        Long productId = 1L;
        int quantity = 15;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Client client = new Client();
        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(10);

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        when(clientUserRepository.findByUserIdAndDeletedFalse(userDTO.getId()))
                .thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client))
                .thenReturn(Optional.of(shoppingCart));
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> shoppingCartService.addToCart(productId, quantity));
    }

    @Test
    void testShowCart_ReturnsShoppingCartDTO() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Client client = new Client();
        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        when(clientUserRepository.findByUserIdAndDeletedFalse(userDTO.getId()))
                .thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client))
                .thenReturn(Optional.of(shoppingCart));
        when(modelMapper.map(shoppingCart, ShoppingCartDTO.class)).thenReturn(shoppingCartDTO);

        ShoppingCartDTO result = shoppingCartService.showCart();

        assertNotNull(result);
        verify(modelMapper, times(1)).map(shoppingCart, ShoppingCartDTO.class);
    }

    @Test
    void testRemoveCartItem_ValidCartItem_RemovesFromCart() throws Exception {
        Long cartItemId = 1L;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Client client = new Client();
        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);

        ShoppingCart shoppingCart = new ShoppingCart();
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        shoppingCart.setItems(cartItemList);

        when(userService.findAuthenticatedUser()).thenReturn(userDTO);
        when(clientUserRepository.findByUserIdAndDeletedFalse(userDTO.getId()))
                .thenReturn(Optional.of(clientUser));
        when(shoppingCartRepository.findByClientId(client))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndDeletedFalse(cartItemId))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.removeCartItem(cartItemId);

        assertTrue(cartItem.isDeleted());
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }
}
