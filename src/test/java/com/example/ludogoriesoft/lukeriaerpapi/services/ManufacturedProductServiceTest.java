package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ManufacturedProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManufacturedProductServiceTest {

    @Mock
    private ManufacturedProductRepository manufacturedProductRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ManufacturedProductService manufacturedProductService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createManufacturedProduct_ShouldSaveProduct() {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(manufacturedProductRepository.save(manufacturedProduct)).thenReturn(manufacturedProduct);

        ManufacturedProduct result = manufacturedProductService.createManufacturedProduct(manufacturedProduct);

        assertNotNull(result);
        verify(manufacturedProductRepository, times(1)).save(manufacturedProduct);
        assertNotNull(result.getManufacture_date());
    }

    @Test
    void createManufacturedProductFromProduct_ShouldSaveProduct() {
        Product product = new Product();
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(null, product, 10, LocalDateTime.now(), false);
        when(manufacturedProductRepository.save(any(ManufacturedProduct.class))).thenReturn(manufacturedProduct);

        ManufacturedProduct result = manufacturedProductService.createManufacturedProductFromProduct(product, 10, LocalDateTime.now(), false);

        assertNotNull(result);
        verify(manufacturedProductRepository, times(1)).save(any(ManufacturedProduct.class));
        assertNotNull(result.getManufacture_date());
    }

    @Test
    void getAllManufacturedProducts_ShouldReturnList() {
        List<ManufacturedProduct> products = Arrays.asList(new ManufacturedProduct(), new ManufacturedProduct());
        when(manufacturedProductRepository.findAll()).thenReturn(products);

        List<ManufacturedProduct> result = manufacturedProductService.getAllManufacturedProducts();

        assertEquals(2, result.size());
        verify(manufacturedProductRepository, times(1)).findAll();
    }

    @Test
    void getManufacturedProductById_ShouldReturnProduct() {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.of(manufacturedProduct));

        Optional<ManufacturedProduct> result = manufacturedProductService.getManufacturedProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(manufacturedProduct, result.get());
        verify(manufacturedProductRepository, times(1)).findById(1L);
    }

    @Test
    void getManufacturedProductById_ShouldReturnEmptyOptional() {
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ManufacturedProduct> result = manufacturedProductService.getManufacturedProductById(1L);

        assertFalse(result.isPresent());
        verify(manufacturedProductRepository, times(1)).findById(1L);
    }

    @Test
    void updateManufacturedProduct_ShouldUpdateProduct() {
        ManufacturedProduct existingProduct = new ManufacturedProduct();
        Product product = new Product();
        ManufacturedProductDTO dto = new ManufacturedProductDTO(1L, 1L, 10, LocalDateTime.now());
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.of(product));
        when(manufacturedProductRepository.save(existingProduct)).thenReturn(existingProduct);

        ManufacturedProduct result = manufacturedProductService.updateManufacturedProduct(1L, dto);

        assertNotNull(result);
        verify(manufacturedProductRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(dto.getProductId());
        verify(manufacturedProductRepository, times(1)).save(existingProduct);
    }

    @Test
    void updateManufacturedProduct_ShouldThrowExceptionIfNotFound() {
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.empty());

        ManufacturedProductDTO dto = new ManufacturedProductDTO(1L, 1L, 10, LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                manufacturedProductService.updateManufacturedProduct(1L, dto)
        );

        assertEquals("ManufacturedProduct with ID 1 not found.", exception.getMessage());
        verify(manufacturedProductRepository, times(1)).findById(1L);
    }

    @Test
    void deleteManufacturedProduct_ShouldMarkAsDeleted() {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.of(manufacturedProduct));

        manufacturedProductService.deleteManufacturedProduct(1L);

        assertTrue(manufacturedProduct.isDeleted());
        verify(manufacturedProductRepository, times(1)).findById(1L);
        verify(manufacturedProductRepository, times(1)).save(manufacturedProduct);
    }
    @Test
    void createManufacturedProduct_ShouldThrowExceptionIfNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                manufacturedProductService.createManufacturedProduct(null)
        );

        assertEquals("Cannot invoke \"com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct.setManufacture_date(java.time.LocalDateTime)\" because \"manufacturedProduct\" is null", exception.getMessage());
        verify(manufacturedProductRepository, never()).save(any());
    }
    @Test
    void getAllManufacturedProducts_ShouldReturnEmptyListWhenNoProductsExist() {
        when(manufacturedProductRepository.findAll()).thenReturn(List.of());

        List<ManufacturedProduct> result = manufacturedProductService.getAllManufacturedProducts();

        assertTrue(result.isEmpty());
        verify(manufacturedProductRepository, times(1)).findAll();
    }





}
