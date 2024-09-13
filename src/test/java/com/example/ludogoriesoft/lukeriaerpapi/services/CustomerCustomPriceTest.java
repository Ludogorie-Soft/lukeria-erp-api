package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        customerCustomPriceDTO = new CustomerCustomPriceDTO();
        customerCustomPriceDTO.setPrice(BigDecimal.valueOf(100));
        customerCustomPriceDTO.setClientId(1L);
        customerCustomPriceDTO.setProductId(1L);

        customerCustomPrice = new CustomerCustomPrice();
        customerCustomPrice.setId(1L);
        customerCustomPrice.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void create_ShouldCreateCustomPrice() throws ChangeSetPersister.NotFoundException {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(customerCustomPriceRepository.save(any(CustomerCustomPrice.class))).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class)).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        // Act
        CustomerCustomPriceDTO result = customerCustomPriceService.create(customerCustomPriceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(result.getPrice(), BigDecimal.valueOf(100));
        verify(customerCustomPriceRepository, times(1)).save(any(CustomerCustomPrice.class));
    }

    @Test
    void create_ShouldThrowException_WhenPriceIsZero() {
        // Arrange
        customerCustomPriceDTO.setPrice(BigDecimal.ZERO);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> customerCustomPriceService.create(customerCustomPriceDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> customerCustomPriceService.create(customerCustomPriceDTO));
        assertEquals("Client does not exist with ID: 1", exception.getMessage());
    }

    @Test
    void update_ShouldUpdateCustomPrice() throws ChangeSetPersister.NotFoundException {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(customerCustomPriceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class)).thenReturn(customerCustomPrice);
        when(customerCustomPriceRepository.save(customerCustomPrice)).thenReturn(customerCustomPrice);
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        // Act
        CustomerCustomPriceDTO result = customerCustomPriceService.update(1L, customerCustomPriceDTO);

        // Assert
        assertNotNull(result);
        verify(customerCustomPriceRepository, times(1)).save(customerCustomPrice);
    }

    @Test
    void update_ShouldThrowException_WhenCustomPriceNotFound() {
        // Arrange
        when(customerCustomPriceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> customerCustomPriceService.update(1L, customerCustomPriceDTO));
    }

    @Test
    void getAllCustomPrices_ShouldReturnAllCustomPrices() {
        // Arrange
        when(customerCustomPriceRepository.findByDeletedFalse()).thenReturn(List.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        // Act
        List<CustomerCustomPriceDTO> result = customerCustomPriceService.getAllCustomPrices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerCustomPriceRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void delete_ShouldMarkCustomPriceAsDeleted() throws ChangeSetPersister.NotFoundException {
        // Arrange
        when(customerCustomPriceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(customerCustomPrice));
        when(modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class)).thenReturn(customerCustomPriceDTO);

        // Act
        CustomerCustomPriceDTO result = customerCustomPriceService.delete(1L);

        // Assert
        assertTrue(customerCustomPrice.isDeleted());
        verify(customerCustomPriceRepository, times(1)).save(customerCustomPrice);
    }

    @Test
    void delete_ShouldThrowException_WhenCustomPriceNotFound() {
        // Arrange
        when(customerCustomPriceRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> customerCustomPriceService.delete(1L));
    }
}
