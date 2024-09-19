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
    void update_ShouldUpdateCustomPrice() throws ChangeSetPersister.NotFoundException {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product))
                .thenReturn(Optional.of(customerCustomPrice));
        when(customerCustomPriceRepository.save(any(CustomerCustomPrice.class))).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class)).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        CustomerCustomPriceDTO result = customerCustomPriceService.update(customerCustomPriceDTO);

        assertNotNull(result);
        verify(customerCustomPriceRepository, times(1)).save(any(CustomerCustomPrice.class));
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
}
