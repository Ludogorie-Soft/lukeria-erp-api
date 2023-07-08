package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.ProductMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, productMapper);
    }

    @Test
    void testToDTO() {
        Product product = new Product();
        ProductDTO productDTO = new ProductDTO();
        when(productMapper.toDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.toDTO(product);

        assertEquals(productDTO, result);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void testToEntity() {
        ProductDTO productDTO = new ProductDTO();
        Product product = new Product();
        when(productMapper.toEntity(productDTO)).thenReturn(product);

        Product result = productService.toEntity(productDTO);

        assertEquals(product, result);
        verify(productMapper, times(1)).toEntity(productDTO);
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product();
        Product product2 = new Product();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(2)).toDto(any(Product.class));
    }

    @Test
    void testGetProductById_ExistingProduct() {
        Long productId = 1L;
        Product product = new Product();
        ProductDTO productDTO = new ProductDTO();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(productDTO, result);
        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void testGetProductById_NonExistingProduct() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> productService.getProductById(productId));

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void testCreateProduct_ValidProductDTO() {
        ProductDTO productDTO = new ProductDTO();
        Plate plateDTO = new Plate();
        plateDTO.setName("Plate Name");
        productDTO.setPlateId(plateDTO);
        Product product = new Product();
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(productDTO, result);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toEntity(productDTO);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void testCreateProduct_BlankPlateName() {
        ProductDTO productDTO = new ProductDTO();
        Plate plateDTO = new Plate();
        plateDTO.setName("");
        productDTO.setPlateId(plateDTO);

        assertThrows(ApiRequestException.class, () -> productService.createProduct(productDTO));

        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toEntity(any());
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void testUpdateProduct_ExistingProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(10.99);
        productDTO.setAvailableQuantity(20);
        Plate plateDTO = new Plate();
        plateDTO.setId(1L);
        productDTO.setPlateId(plateDTO);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toDto(existingProduct)).thenReturn(productDTO);

        ProductDTO result = productService.updateProduct(productId, productDTO);

        assertNotNull(result);
        assertEquals(productDTO, result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
        verify(productMapper, times(1)).toDto(existingProduct);
    }
    @Test
    void testUpdateProduct_NonExistingProduct() {
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> productService.updateProduct(productId, productDTO));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void testUpdateProduct_InvalidProductData() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        ProductDTO productDTO = new ProductDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        assertThrows(ApiRequestException.class, () -> productService.updateProduct(productId, productDTO));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void testDeleteProduct_ExistingProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(existingProduct);
    }

    @Test
    void testDeleteProduct_NonExistingProduct() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> productService.deleteProduct(productId));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).delete(any());
    }

}