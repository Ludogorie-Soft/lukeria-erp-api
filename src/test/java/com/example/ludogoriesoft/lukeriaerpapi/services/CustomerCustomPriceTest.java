package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerCustomPriceTest {

    @InjectMocks
    private CustomerCustomPriceService customerCustomPriceService;

    @Mock
    private CustomerCustomPriceRepository customerCustomPriceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    private CustomerCustomPriceDTO customerCustomPriceDTO;
    private CustomerCustomPrice customerCustomPrice;
    private Client client;
    private Product product;

    @BeforeEach
    void setUp() {
        customerCustomPriceDTO = new CustomerCustomPriceDTO();
        customerCustomPriceDTO.setPrice(BigDecimal.valueOf(100));
        customerCustomPriceDTO.setClientId(1L);
        customerCustomPriceDTO.setProductId(1L);

        customerCustomPrice = new CustomerCustomPrice();
        customerCustomPrice.setId(1L);
        customerCustomPrice.setPrice(BigDecimal.valueOf(100));

        client = new Client();
        client.setId(1L);

        product = new Product();
        product.setId(1L);
    }

    @Test
    void create_ShouldCreateCustomPrice() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(customerCustomPriceRepository.save(any(CustomerCustomPrice.class))).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class)).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        CustomerCustomPriceDTO result = customerCustomPriceService.create(customerCustomPriceDTO);

        assertNotNull(result);
        assertEquals(result.getPrice(), BigDecimal.valueOf(100));
        verify(customerCustomPriceRepository, times(1)).save(any(CustomerCustomPrice.class));
    }

    @Test
    void create_ShouldThrowException_WhenPriceIsZero() {
        customerCustomPriceDTO.setPrice(BigDecimal.ZERO);

        ValidationException exception = assertThrows(ValidationException.class, () -> customerCustomPriceService.create(customerCustomPriceDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenClientDoesNotExist() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> customerCustomPriceService.create(customerCustomPriceDTO));
        assertEquals("Client does not exist with ID: 1", exception.getMessage());
    }
    @Test
    void create_ShouldThrowException_WhenClientIdIsNull() {
        customerCustomPriceDTO.setClientId(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerCustomPriceService.create(customerCustomPriceDTO)
        );
        assertEquals("Client ID cannot be null", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenProductDoesNotExist() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerCustomPriceService.create(customerCustomPriceDTO)
        );
        assertEquals("Product does not exist with ID: 1", exception.getMessage());
    }
    @Test
    void update_ShouldUpdateCustomPrice() throws ChangeSetPersister.NotFoundException {
        // Arrange: Set up the necessary mock behaviors

        // Mock client and product repositories for existence checks in validation
        when(clientRepository.existsById(1L)).thenReturn(true); // Mock client existence check
        when(productRepository.existsById(1L)).thenReturn(true); // Mock product existence check

        // Mock retrieval of client and product entities
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));

        // Mock retrieval of existing customer custom price
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.of(customerCustomPrice));

        // Mock the saving and mapping of the updated customer custom price
        when(customerCustomPriceRepository.save(any(CustomerCustomPrice.class))).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class)).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        // Act: Call the service's update method
        CustomerCustomPriceDTO result = customerCustomPriceService.update(customerCustomPriceDTO);

        // Assert: Verify the result and interactions
        assertNotNull(result); // Ensure the result is not null
        assertEquals(customerCustomPriceDTO, result); // Verify the returned DTO matches the mock DTO

        // Verify that the repository save method was called exactly once with the expected argument
        verify(customerCustomPriceRepository, times(1)).save(any(CustomerCustomPrice.class));

        // Verify that the mapping was performed from DTO to entity and back to DTO
        verify(modelMapper, times(1)).map(customerCustomPriceDTO, CustomerCustomPrice.class);
        verify(modelMapper, times(1)).map(customerCustomPrice, CustomerCustomPriceDTO.class);
    }


    @Test
    void update_ShouldThrowException_WhenCustomPriceNotFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> customerCustomPriceService.update(customerCustomPriceDTO));
    }

    @Test
    void getAllCustomPrices_ShouldReturnAllCustomPrices() {
        when(customerCustomPriceRepository.findByDeletedFalse()).thenReturn(List.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        List<CustomerCustomPriceDTO> result = customerCustomPriceService.getAllCustomPrices();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerCustomPriceRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void delete_ShouldMarkCustomPriceAsDeleted() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.of(customerCustomPrice));

        CustomerCustomPriceDTO result = customerCustomPriceService.delete(1L, 1L);

        assertTrue(customerCustomPrice.isDeleted());
        verify(customerCustomPriceRepository, times(1)).save(customerCustomPrice);
    }

    @Test
    void delete_ShouldThrowException_WhenCustomPriceNotFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> customerCustomPriceService.delete(1L, 1L));
    }

    @Test
    void allProductWithCustomPriceForClient_ShouldReturnCustomPricesForClient() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(customerCustomPriceRepository.findByClientIdAndDeletedFalse(client)).thenReturn(List.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        List<CustomerCustomPriceDTO> result = customerCustomPriceService.allProductWithCustomPriceForClient(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerCustomPriceRepository, times(1)).findByClientIdAndDeletedFalse(client);
    }
    @Test
    void findByClientAndProduct_ShouldReturnCustomPrice_WhenFound() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product)).thenReturn(Optional.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        CustomerCustomPriceDTO result = customerCustomPriceService.findByClientAndProduct(1L, 1L);

        assertNotNull(result);
        assertEquals(result.getPrice(), BigDecimal.valueOf(100));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(productRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(customerCustomPriceRepository, times(1)).findByClientIdAndProductIdAndDeletedFalse(client, product);
    }

    @Test
    void findByClientAndProduct_ShouldThrowNotFoundException_WhenClientNotFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () ->
                customerCustomPriceService.findByClientAndProduct(1L, 1L));

        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(customerCustomPriceRepository);
    }

    @Test
    void findByClientAndProduct_ShouldThrowNotFoundException_WhenProductNotFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () ->
                customerCustomPriceService.findByClientAndProduct(1L, 1L));

        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(productRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(customerCustomPriceRepository);
    }

    @Test
    void findByClientAndProduct_ShouldThrowNotFoundException_WhenCustomPriceNotFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () ->
                customerCustomPriceService.findByClientAndProduct(1L, 1L));

        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(productRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(customerCustomPriceRepository, times(1)).findByClientIdAndProductIdAndDeletedFalse(client, product);
    }

}
