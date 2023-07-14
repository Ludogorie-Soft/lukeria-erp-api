package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, modelMapper);
    }


    @Test
     void testGetAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        Plate plate = new Plate();
        plate.setId(1L);
        products.add(new Product(1L, 10.0, 5,  false));
        products.add(new Product(2L, 15.0, 3,  false));

        when(productRepository.findByDeletedFalse()).thenReturn(products);

        when(modelMapper.map(products.get(0), ProductDTO.class)).thenReturn(new ProductDTO(1L, 10.0, 5));
        when(modelMapper.map(products.get(1), ProductDTO.class)).thenReturn(new ProductDTO(2L, 15.0, 3));

        // Act
        List<ProductDTO> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(10.0, result.get(0).getPrice());
        assertEquals(5, result.get(0).getAvailableQuantity());
        assertEquals(2L, result.get(1).getId());
        assertEquals(15.0, result.get(1).getPrice());
        assertEquals(3, result.get(1).getAvailableQuantity());

    }

    @Test
     void testGetProductById_ExistingProduct() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        Plate plate=new Plate();
        plate.setId(1L);
        Product product = new Product(productId, 10.0, 5, false);
        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(product));

        ProductDTO expectedProductDTO = new ProductDTO(productId, 10.0, 5);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(expectedProductDTO);

        // Act
        ProductDTO result = productService.getProductById(productId);

        // Assert
        assertEquals(expectedProductDTO, result);
    }

    @Test
     void testGetProductById_NonExistingProduct() {
        // Arrange
        Long nonExistingProductId = 10L;
        when(productRepository.findByIdAndDeletedFalse(nonExistingProductId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> productService.getProductById(nonExistingProductId));
    }

    @Test
     void testCreateProduct_ValidProduct() {
        Plate plate=new Plate();
        plate.setId(1L);
        ProductDTO productDTO = new ProductDTO(1L, 10.0, 5);
        Product product = new Product(1L, 10.0, 5, false);

        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(productDTO);

        // Assert
        assertEquals(productDTO, result);
    }

    @Test
     void testCreateProduct_InvalidPrice() {
        // Arrange
        Plate plate=new Plate();
        plate.setId(1L);
        ProductDTO productDTO = new ProductDTO(1L, 0.0, 5);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.createProduct(productDTO));
    }

    @Test
     void testCreateProduct_InvalidQuantity() {
        Plate plate=new Plate();
        plate.setId(1L);
        ProductDTO productDTO = new ProductDTO(1L, 10.0, 0);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.createProduct(productDTO));
    }



    @Test
     void testUpdateProduct_ValidProduct() throws ChangeSetPersister.NotFoundException {
        Plate plate=new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, 20.0, 10);
        Product existingProduct = new Product(productId, 10.0, 5, false);
        Product updatedProduct = new Product(productId, 20.0, 10, false);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(modelMapper.map(updatedProduct, ProductDTO.class)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.updateProduct(productId, productDTO);

        // Assert
        assertEquals(productDTO, result);

    }

    @Test
     void testUpdateProduct_InvalidPrice() {
        Plate plate=new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, 0.0, 10);
        Product existingProduct = new Product(productId, 10.0, 5, false);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));


    }

    @Test
     void testUpdateProduct_InvalidQuantity()  {
        Plate plate=new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, 20.0, 0 );
        Product existingProduct = new Product(productId, 10.0, 5,  false);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));


    }

    @Test
     void testUpdateProduct_MissingPlate() {
        Plate plate=new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, 20.0, 10);
        Product existingProduct = new Product(productId, 10.0, 5, false);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(NullPointerException.class, () -> productService.updateProduct(productId, productDTO));

    }

    @Test
     void testUpdateProduct_NonExistingProduct() {
        Plate plate=new Plate();
        plate.setId(1L);
        Long nonExistingProductId = 10L;
        ProductDTO productDTO = new ProductDTO(nonExistingProductId, 20.0, 10);

        when(productRepository.findByIdAndDeletedFalse(nonExistingProductId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> productService.updateProduct(nonExistingProductId, productDTO));

        verify(productRepository, never()).save(any());
    }


    @Test
    void testDeleteProduct_ExistingProduct() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Plate plate = new Plate();
        plate.setId(1L);
        Long productId = 1L;
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(product).setDeleted(true);
        verify(productRepository).save(product);
    }


}