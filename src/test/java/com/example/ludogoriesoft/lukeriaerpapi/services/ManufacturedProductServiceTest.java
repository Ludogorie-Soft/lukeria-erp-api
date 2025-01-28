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
import java.util.ArrayList;
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
    void setup() {
        MockitoAnnotations.openMocks(this);
        manufacturedProductService = new ManufacturedProductService(manufacturedProductRepository, productRepository);
    }

    @Test
    void testCreateManufacturedProduct() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(null, product, 10, null, false);
        manufacturedProduct.setManufacture_date(LocalDateTime.now());

        when(manufacturedProductRepository.save(manufacturedProduct)).thenReturn(manufacturedProduct);

        // Act
        ManufacturedProduct result = manufacturedProductService.createManufacturedProduct(manufacturedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(manufacturedProduct, result);
        verify(manufacturedProductRepository, times(1)).save(manufacturedProduct);
    }

    @Test
    void testCreateManufacturedProductFromProduct() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        int quantity = 10;
        LocalDateTime manufactureDate = LocalDateTime.now();
        boolean deleted = false;

        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(null, product, quantity, manufactureDate, deleted);
        when(manufacturedProductRepository.save(any(ManufacturedProduct.class))).thenReturn(manufacturedProduct);

        // Act
        ManufacturedProduct result = manufacturedProductService.createManufacturedProductFromProduct(product, quantity, manufactureDate, deleted);

        // Assert
        assertNotNull(result);
        assertEquals(product, result.getProduct());
        assertEquals(quantity, result.getQuantity());
        verify(manufacturedProductRepository, times(1)).save(any(ManufacturedProduct.class));
    }

    @Test
    void testGetAllManufacturedProducts() {
        // Arrange
        List<ManufacturedProduct> manufacturedProducts = new ArrayList<>();
        manufacturedProducts.add(new ManufacturedProduct(1L, new Product(), 10, LocalDateTime.now(), false));
        manufacturedProducts.add(new ManufacturedProduct(2L, new Product(), 20, LocalDateTime.now(), true));

        when(manufacturedProductRepository.findAll()).thenReturn(manufacturedProducts);

        // Act
        List<ManufacturedProduct> result = manufacturedProductService.getAllManufacturedProducts();

        // Assert
        assertEquals(2, result.size());
        verify(manufacturedProductRepository, times(1)).findAll();
    }

    @Test
    void testGetManufacturedProductById_Existing() {
        // Arrange
        Long id = 1L;
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct(id, new Product(), 10, LocalDateTime.now(), false);
        when(manufacturedProductRepository.findById(id)).thenReturn(Optional.of(manufacturedProduct));

        // Act
        Optional<ManufacturedProduct> result = manufacturedProductService.getManufacturedProductById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(manufacturedProduct, result.get());
        verify(manufacturedProductRepository, times(1)).findById(id);
    }

    @Test
    void testGetManufacturedProductById_NonExisting() {
        // Arrange
        Long id = 1L;
        when(manufacturedProductRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ManufacturedProduct> result = manufacturedProductService.getManufacturedProductById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(manufacturedProductRepository, times(1)).findById(id);
    }

//    @Test
//    public void testUpdateManufacturedProduct_Success() {
//        ManufacturedProduct existingProduct;
//        ManufacturedProductDTO updatedProductDTO;
//        existingProduct = new ManufacturedProduct();
//        existingProduct.setId(1L);
//        existingProduct.setQuantity(10);
//        existingProduct.setManufacture_date(LocalDateTime.now());
//        existingProduct.setDeleted(false);
//
//        updatedProductDTO = new ManufacturedProductDTO();
//        updatedProductDTO.setId(1L);
//        updatedProductDTO.setProductId(2L);
//        updatedProductDTO.setQuantity(20);
//        updatedProductDTO.setManufactureDate(LocalDateTime.now().plusDays(1));
//
//        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
//
//        ManufacturedProduct updatedProduct = manufacturedProductService.updateManufacturedProduct(1L, updatedProductDTO);
//
//        assertEquals(20, updatedProduct.getQuantity());
//        assertEquals(updatedProductDTO.getManufactureDate(), updatedProduct.getManufacture_date());
//        assertFalse(updatedProduct.isDeleted());
//        verify(manufacturedProductRepository).save(existingProduct);
//    }

    @Test
    public void testUpdateManufacturedProduct_ProductNotFound() {
        ManufacturedProduct existingProduct;
        ManufacturedProductDTO updatedProductDTO;
        existingProduct = new ManufacturedProduct();
        existingProduct.setId(1L);
        existingProduct.setQuantity(10);
        existingProduct.setManufacture_date(LocalDateTime.now());
        existingProduct.setDeleted(false);

        updatedProductDTO = new ManufacturedProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setProductId(2L);
        updatedProductDTO.setQuantity(20);
        updatedProductDTO.setManufactureDate(LocalDateTime.now().plusDays(1));
        when(manufacturedProductRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manufacturedProductService.updateManufacturedProduct(1L, updatedProductDTO);
        });

        assertEquals("ManufacturedProduct with ID 1 not found.", exception.getMessage());
    }

    @Test
    void testDeleteManufacturedProduct() {
        // Arrange
        Long id = 1L;
        ManufacturedProduct existingProduct = new ManufacturedProduct(id, new Product(), 10, LocalDateTime.now(), false);

        when(manufacturedProductRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        // Act
        manufacturedProductService.deleteManufacturedProduct(id);

        // Assert
        assertTrue(existingProduct.isDeleted());
        verify(manufacturedProductRepository, times(1)).findById(id);
        verify(manufacturedProductRepository, times(1)).save(existingProduct);
    }
}

