package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PackageRepository packageRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ProductService productService;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private CartonRepository cartonRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, packageRepository, modelMapper, plateRepository, cartonRepository);
    }

    @Test
    void testGetAllProducts() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        // Arrange
        List<Product> products = new ArrayList<>();
        Plate plate = new Plate();
        plate.setId(1L);
        products.add(new Product(1L, aPackage, BigDecimal.valueOf(10.0), 5, false, "L111",true,"barcode"));
        products.add(new Product(2L, aPackage, BigDecimal.valueOf(15.0), 3, false, "L111",true,"barcode"));

        when(productRepository.findByDeletedFalse()).thenReturn(products);

        when(modelMapper.map(products.get(0), ProductDTO.class)).thenReturn(new ProductDTO(1L, BigDecimal.valueOf(10.0), aPackage.getId(), 5, "L111",true,"barcode"));
        when(modelMapper.map(products.get(1), ProductDTO.class)).thenReturn(new ProductDTO(2L, BigDecimal.valueOf(15.0), aPackage.getId(), 3, "L111",true,"barcode"));

        // Act
        List<ProductDTO> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(BigDecimal.valueOf(10.0), result.get(0).getPrice());
        assertEquals(5, result.get(0).getAvailableQuantity());
        assertEquals(2L, result.get(1).getId());
        assertEquals(BigDecimal.valueOf(15.0), result.get(1).getPrice());
        assertEquals(3, result.get(1).getAvailableQuantity());

    }

    @Test
    void testGetProductById_ExistingProduct() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Package aPackage = new Package();
        aPackage.setId(1L);
        Long productId = 1L;
        Plate plate = new Plate();
        plate.setId(1L);
        Product product = new Product(productId, aPackage, BigDecimal.valueOf(10.0), 5, false, "L111",true,"barcode");
        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(product));

        ProductDTO expectedProductDTO = new ProductDTO(productId, BigDecimal.valueOf(10.0), aPackage.getId(), 5, "L111",true,"barcode");
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
    void testCreateProduct_InvalidQuantity() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        Plate plate = new Plate();
        plate.setId(1L);
        ProductDTO productDTO = new ProductDTO(1L, BigDecimal.valueOf(10.0), aPackage.getId(), 0, "L111",true,"barcode");

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void testUpdateProduct_ReturnsUpdatedProductDTO() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setPrice(BigDecimal.valueOf(9.99));
        productDTO.setAvailableQuantity(20);
        productDTO.setPackageId(1L);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));
        when(packageRepository.existsById(productDTO.getPackageId())).thenReturn(true);
        when(modelMapper.map(productDTO, Product.class)).thenReturn(existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(modelMapper.map(existingProduct, ProductDTO.class)).thenReturn(productDTO);


        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);


        assertNotNull(updatedProductDTO);

    }

    @Test
    void testCreateProduct_ZeroPrice_ValidationException() {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(BigDecimal.ZERO);
        productDTO.setAvailableQuantity(5);
        productDTO.setPackageId(1L);

        // Act & Assert
        assertThrows(ValidationException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void testCreateProduct_ReturnsCreatedProductDTO() {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(BigDecimal.valueOf(9.99));
        productDTO.setAvailableQuantity(20);
        productDTO.setPackageId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(1L); // Задайте ID на записания продукт

        when(packageRepository.existsById(productDTO.getPackageId())).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(modelMapper.map(productDTO, Product.class)).thenReturn(new Product());
        when(modelMapper.map(savedProduct, ProductDTO.class)).thenReturn(productDTO);

        // Act
        ProductDTO createdProductDTO = productService.createProduct(productDTO);

        // Assert
        assertNotNull(createdProductDTO);

    }

    @Test
    void testUpdateProduct_ProductNotFound_ChangeSetPersisterNotFoundException() {
        // Arrange
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(new BigDecimal(10));
        productDTO.setAvailableQuantity(5);
        productDTO.setPackageId(1L);

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));
    }


    @Test
    void testUpdateProduct_InvalidPrice() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, BigDecimal.valueOf(0.0), aPackage.getId(), 10, "L111",true,"barcode");
        Product existingProduct = new Product(productId, aPackage, BigDecimal.valueOf(10.0), 5, false, "L111",true,"barcode");

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));


    }

    @Test
    void testUpdateProduct_InvalidQuantity() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        Plate plate = new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, BigDecimal.valueOf(20.0), aPackage.getId(), 0, "L111",true,"barcode");
        Product existingProduct = new Product(productId, aPackage, BigDecimal.valueOf(10.0), 5, false, "L111",true,"barcode");

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));


    }

    @Test
    void testUpdateProduct_MissingPlate() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        Plate plate = new Plate();
        plate.setId(1L);
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO(productId, BigDecimal.valueOf(20.0), aPackage.getId(), 10, "L111",true,"barcode");
        Product existingProduct = new Product(productId, aPackage, BigDecimal.valueOf(10.0), 5, false, "L111",true,"barcode");

        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(existingProduct));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(productId, productDTO));

    }

    @Test
    void testUpdateProduct_NonExistingProduct() {
        Package aPackage = new Package();
        aPackage.setId(1L);
        Plate plate = new Plate();
        plate.setId(1L);
        Long nonExistingProductId = 10L;
        ProductDTO productDTO = new ProductDTO(nonExistingProductId, BigDecimal.valueOf(20.0), aPackage.getId(), 10, "L111",true,"barcode");

        when(productRepository.findByIdAndDeletedFalse(nonExistingProductId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> productService.updateProduct(nonExistingProductId, productDTO));

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
    @Test
    void testGetProductsForSale() {
        // Arrange
        Package aPackage = new Package();
        aPackage.setId(1L);

        List<Product> availableProducts = new ArrayList<>();
        availableProducts.add(new Product(1L, aPackage, BigDecimal.valueOf(10.0), 10, false, "L111", true,"barcode"));
        availableProducts.add(new Product(2L, aPackage, BigDecimal.valueOf(20.0), 15, false, "L112", true,"barcode"));

        when(productRepository.getAvailableProductsForSale()).thenReturn(availableProducts);

        // Mock the modelMapper to convert Product to ProductDTO
        ProductDTO productDTO1 = new ProductDTO(1L, BigDecimal.valueOf(10.0), aPackage.getId(), 10, "L111", true,"barcode");
        ProductDTO productDTO2 = new ProductDTO(2L, BigDecimal.valueOf(20.0), aPackage.getId(), 15, "L112", true,"barcode");

        Type listType = new TypeToken<List<ProductDTO>>() {}.getType();
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(productDTO1);
        productDTOList.add(productDTO2);

        when(modelMapper.map(availableProducts, listType)).thenReturn(productDTOList);

        // Act
        List<ProductDTO> result = productService.getProductsForSale();

        // Assert
        assertEquals(2, result.size());
        assertEquals(productDTO1, result.get(0));
        assertEquals(productDTO2, result.get(1));

        verify(productRepository, times(1)).getAvailableProductsForSale();
        verify(modelMapper, times(1)).map(availableProducts, listType);
    }


    @Test
    void testProduceProduct() throws ChangeSetPersister.NotFoundException {
        Long productId = 1L;
        int producedQuantity = 10;

        Product product = new Product();
        product.setId(productId);
        product.setAvailableQuantity(50);


        Plate plate = new Plate();
        plate.setId(1L);
        plate.setAvailableQuantity(100);

        Carton carton = new Carton();
        carton.setId(1L);
        carton.setAvailableQuantity(100);

        Package aPackage = new Package();
        aPackage.setId(1L);
        aPackage.setCartonId(carton);
        aPackage.setPlateId(plate);
        aPackage.setPiecesCarton(1);
        product.setPackageId(aPackage);

        Mockito.when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(Optional.of(product));
        Mockito.when(packageRepository.findByIdAndDeletedFalse(aPackage.getId())).thenReturn(Optional.of(aPackage));
        Mockito.when(plateRepository.findByIdAndDeletedFalse(plate.getId())).thenReturn(Optional.of(plate));
        Mockito.when(cartonRepository.findByIdAndDeletedFalse(carton.getId())).thenReturn(Optional.of(carton));

        ProductDTO result = productService.produceProduct(productId, producedQuantity);

        assertEquals(60, product.getAvailableQuantity());
        assertEquals(-10, aPackage.getAvailableQuantity());
        assertEquals(90, plate.getAvailableQuantity());
        assertEquals(90, carton.getAvailableQuantity());
    }
    @Test
    void testGetProductByPackage_PackageExists() {
        // Arrange
        Long packageId = 1L;
        Package pkg = new Package();
        Product product = new Product();
        ProductDTO productDTO = new ProductDTO();

        when(packageRepository.findById(packageId)).thenReturn(Optional.of(pkg));
        when(productRepository.findByPackageId(pkg)).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.getProductByPackage(packageId);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO, result);
        verify(packageRepository, times(2)).findById(packageId);
        verify(productRepository).findByPackageId(pkg);
        verify(modelMapper).map(product, ProductDTO.class);
    }

    @Test
    void testGetProductByPackage_PackageNotFound() {
        // Arrange
        Long packageId = 1L;
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.getProductByPackage(packageId);
        });

        verify(packageRepository).findById(packageId);
        verifyNoInteractions(productRepository, modelMapper);
    }

}