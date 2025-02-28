package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import com.example.ludogoriesoft.lukeriaerpapi.utils.MaterialOrderMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MaterialOrderServiceTest {
    @InjectMocks
    private MaterialOrderService materialOrderService;

    @Mock
    private MaterialOrderRepository materialOrderRepository;
    @Mock
    private  MaterialOrderItemRepository materialOrderItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MaterialOrderMapper materialOrderMapper;

    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private CartonRepository cartonRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PackageRepository packageRepository;

    private MaterialOrder existingMaterialOrder;
    private MaterialOrderDTO materialOrderDTO;

    @BeforeEach
    void setup() {
        existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(1L);
        // Set other fields as necessary

        materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setId(1L);
        // Set other fields as necessary
    }

    @Test
    void testGetAllMaterialOrders() {
        // Arrange: Stub the repository to return an empty list
        when(materialOrderRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());

        // Act: Call the method under test
        List<MaterialOrderDTO> result = materialOrderService.getAllMaterialOrders();

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(0, result.size());
    }


    @Test
    void testGetMaterialOrderById() throws ChangeSetPersister.NotFoundException {
        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingMaterialOrder));
        when(modelMapper.map(any(), eq(MaterialOrderDTO.class))).thenReturn(materialOrderDTO);

        MaterialOrderDTO result = null;
        try {
            result = materialOrderService.getMaterialOrderById(1L);
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetMaterialOrderById_NotFound() {
        // Arrange: Stub the repository to return an empty optional
        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // Act & Assert: Ensure the correct exception is thrown when the service method is called
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            materialOrderService.getMaterialOrderById(1L);
        });
    }


    @Test
    void testCreateMaterialOrder() {
        // Arrange: Stub the necessary methods
        when(materialOrderMapper.toEntity(materialOrderDTO)).thenReturn(existingMaterialOrder);
        when(materialOrderRepository.save(existingMaterialOrder)).thenReturn(existingMaterialOrder);
        // The following line is commented out as it might be unnecessary for the current flow
        // when(modelMapper.map(existingMaterialOrder, MaterialOrderDTO.class)).thenReturn(materialOrderDTO);

        // Act: Call the method under test
        MaterialOrderDTO result = materialOrderService.createMaterialOrder(materialOrderDTO);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        // You can also verify that the repository save method was called
        verify(materialOrderRepository, times(1)).save(existingMaterialOrder);
    }

    @Test
    void testUpdateMaterialOrder() throws ChangeSetPersister.NotFoundException {
        // Arrange
        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingMaterialOrder));
        when(plateRepository.existsById(1L)).thenReturn(true); // Mocking existence of Plate

        // Setup existingMaterialOrder with necessary fields
        existingMaterialOrder.setOrderDate(LocalDate.now().atStartOfDay());  // Assuming this field is necessary
        // Add other setup as necessary if you have required fields

        // Create and setup the MaterialOrderItemDTO
        MaterialOrderItemDTO itemDTO = new MaterialOrderItemDTO();
        itemDTO.setMaterialType(MaterialType.PLATE);
        itemDTO.setOrderedQuantity(10);
        itemDTO.setMaterialId(1L); // Valid MaterialId

        // Create a corresponding MaterialOrderItem instance for mapping
        MaterialOrderItem itemEntity = new MaterialOrderItem();
        itemEntity.setMaterialId(itemDTO.getMaterialId());
        itemEntity.setMaterialType(itemDTO.getMaterialType());
        // Other fields can be set as needed

        // Setup the mapper behavior
        when(modelMapper.map(any(), eq(MaterialOrderItem.class))).thenReturn(itemEntity);
        when(modelMapper.map(existingMaterialOrder, MaterialOrderDTO.class)).thenReturn(new MaterialOrderDTO()); // Mocking return of DTO

        MaterialOrderDTO updatedOrderDTO = new MaterialOrderDTO();
        updatedOrderDTO.setItems(Collections.singletonList(itemDTO));
        updatedOrderDTO.setStatus("UPDATED_STATUS");

        // Act
        MaterialOrderDTO result = materialOrderService.updateMaterialOrder(1L, updatedOrderDTO);

        // Assert
        assertNotNull(result, "Expected updateMaterialOrder to return a non-null result");
//        assertEquals("UPDATED_STATUS", result.getStatus());
    }





    @Test
    void testDeleteMaterialOrder() {
        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingMaterialOrder));

        assertDoesNotThrow(() -> materialOrderService.deleteMaterialOrder(1L));
        verify(materialOrderRepository, times(1)).save(existingMaterialOrder);
    }

    @Test
    void testValidate_WithEmptyItems() {
        MaterialOrderDTO invalidOrderDTO = new MaterialOrderDTO();
        invalidOrderDTO.setItems(Collections.emptyList());

        assertThrows(ValidationException.class, () -> {
            materialOrderService.validate(invalidOrderDTO);
        });
    }

    @Test
    void testValidate_WithInvalidMaterialId() {
        MaterialOrderItemDTO invalidItemDTO = new MaterialOrderItemDTO();
        invalidItemDTO.setMaterialType(MaterialType.CARTON);
        invalidItemDTO.setMaterialId(999L); // Assuming this ID doesn't exist

        MaterialOrderDTO invalidOrderDTO = new MaterialOrderDTO();
        invalidOrderDTO.setItems(Collections.singletonList(invalidItemDTO));

        assertThrows(ValidationException.class, () -> {
            materialOrderService.validate(invalidOrderDTO);
        });
    }
    @Test
    void testValidate_WithEmptyItems_ShouldThrowValidationException() {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setItems(Collections.emptyList());

        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_WithInvalidMaterialId_ShouldThrowValidationException() {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        MaterialOrderItemDTO itemDTO = new MaterialOrderItemDTO();
        itemDTO.setMaterialType(MaterialType.CARTON);
        itemDTO.setMaterialId(999L); // Assume this ID is not valid
        materialOrderDTO.setItems(Collections.singletonList(itemDTO));

        when(cartonRepository.existsById(999L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }
//    @Test
//    void testGetAllOrderProductsByOrderId_WithValidId() {
//        Long orderId = 1L;
//
//        // Mocking the Package, Product, and OrderProduct
//        Package packageEntity = new Package();
//        packageEntity.setId(1L);
//        packageEntity.setAvailableQuantity(5); // Example available quantity
//        packageEntity.setPiecesCarton(2);
//
//        // Mocking an OrderProduct linked to the order
//        OrderProduct orderProduct = new OrderProduct();
//        Order order = new Order();
//        order.setId(orderId);
//        orderProduct.setOrderId(order); // This sets the order ID
//        orderProduct.setPackageId(packageEntity); // Set the package for this order product
//        orderProduct.setNumber(8); // Number of products in the order
//
//        // Mocking repositories
//        when(orderProductRepository.findByDeletedFalse()).thenReturn(List.of(orderProduct));
//
//        // Mocking the product repository to ensure it finds the package
//        Product productEntity = new Product();
//        productEntity.setAvailableQuantity(10); // Product must have sufficient quantity
//        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity)).thenReturn(Optional.of(productEntity));
//
//        // Execute the method under test
//        List<MaterialOrderItemDTO> result = materialOrderService.getAllOrderProductsByOrderId(orderId);
//
//        // Validate the results
//        assertNotNull(result);
//        assertEquals(1, result.size()); // Expecting one result based on setup
//
//        // Further assertions based on what should happen in your createMaterialOrder logic
//        MaterialOrderItemDTO item = result.get(1);
//        assertEquals(packageEntity.getId(), item.getMaterialId());
//    }

    @Test
    void testGetProductsByPackageId_WithInsufficientInventory() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Package packageEntity = new Package();
        Plate plate = new Plate();
        plate.setId(1L);
        plate.setAvailableQuantity(100);
        packageEntity.setPlateId(plate);
        Carton carton = new Carton();
        carton.setId(1L);
        carton.setAvailableQuantity(2);
        packageEntity.setCartonId(carton);
        packageEntity.setPiecesCarton(100);
        packageEntity.setId(1L);
        packageEntity.setAvailableQuantity(5); // Available quantity less than requested
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setPackageId(packageEntity);
        orderProduct.setNumber(10); // Requesting more than available

        orderProducts.add(orderProduct);

        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity)).thenReturn(Optional.of(new Product()));
//        when(packageEntity.getPlateId().getAvailableQuantity()).thenReturn()

        // Execute
        List<MaterialOrderItemDTO> result = materialOrderService.getProductsByPackageId(orderProducts);

        // Check the result
        assertNotNull(result);
        assertEquals(1, result.size()); // Expecting one item based on created logic
    }

    @Test
    void testGetProductsByPackageId_WithValidInventory() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Package packageEntity = new Package();
        Plate plate = new Plate();
        plate.setId(1L);
        plate.setAvailableQuantity(100);
        packageEntity.setPlateId(plate);
        Carton carton = new Carton();
        carton.setId(1L);
        carton.setAvailableQuantity(2);
        packageEntity.setCartonId(carton);
        packageEntity.setPiecesCarton(100);
        packageEntity.setId(1L);
        packageEntity.setAvailableQuantity(5); // Available quantity less than requested
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setPackageId(packageEntity);
        orderProduct.setNumber(10); // Requesting more than available

        orderProducts.add(orderProduct);

        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity)).thenReturn(Optional.of(new Product()));

        // Execute
        List<MaterialOrderItemDTO> result = materialOrderService.getProductsByPackageId(orderProducts);

        // Check the result
        assertNotNull(result);
        assertEquals(1, result.size()); // No material order should be created
    }
    @Test
    void testCalculateCartonInsufficientNumbers_WithValidPackage() {
        Package packageEntity = new Package();
        packageEntity.setPiecesCarton(5);
        var cartonId = new Carton(); // Assuming you have a Carton entity
        cartonId.setAvailableQuantity(20);
        packageEntity.setCartonId(cartonId);

        int result = materialOrderService.calculateCartonInsufficientNumbers(packageEntity);

        assertEquals(100, result); // 5 * 20 = 100
    }

    @Test
    void testCalculateCartonInsufficientNumbers_WithNoCarton() {
        Package packageEntity = new Package();
        packageEntity.setPiecesCarton(5);

        // Assuming the cartonId is null or there won't be a quantity
        Exception exception = assertThrows(RuntimeException.class, () -> {
            materialOrderService.calculateCartonInsufficientNumbers(packageEntity);
        });

        assertEquals("Cannot invoke \"com.example.ludogoriesoft.lukeriaerpapi.models.Carton.getAvailableQuantity()\" because the return value of \"com.example.ludogoriesoft.lukeriaerpapi.models.Package.getCartonId()\" is null", exception.getMessage());
    }
    @Test
    void testCalculatePlateInsufficientNumbers_WithValidPackage() {
        Package packageEntity = new Package();
        Plate plate = new Plate();
        plate.setAvailableQuantity(10); // Mock a valid quantity
        packageEntity.setPlateId(plate);

        int result = materialOrderService.calculatePlateInsufficientNumbers(packageEntity);

        assertEquals(10, result); // Expect the available quantity of plates
    }

    @Test
    void testCalculatePlateInsufficientNumbers_WithNullPlate() {
        Package packageEntity = new Package();
        packageEntity.setPlateId(null); // No plate associated

        Exception exception = assertThrows(RuntimeException.class, () -> {
            materialOrderService.calculatePlateInsufficientNumbers(packageEntity);
        });

        assertEquals("Cannot invoke \"com.example.ludogoriesoft.lukeriaerpapi.models.Plate.getAvailableQuantity()\" because the return value of \"com.example.ludogoriesoft.lukeriaerpapi.models.Package.getPlateId()\" is null", exception.getMessage());
    }
    @Test
    void testCalculatePackageInsufficientNumbers_WithValidPackage() {
        Package packageEntity = new Package();
        packageEntity.setAvailableQuantity(20); // Mock a valid package quantity

        int result = materialOrderService.calculatePackageInsufficientNumbers(packageEntity);

        assertEquals(20, result); // Expect the available quantity of the package
    }
    @Test
    void testCreateMaterialOrder_ValidInput() {
        List<MaterialOrderItemDTO> materialsForOrder = new ArrayList<>();
        MaterialType type = MaterialType.PACKAGE;
        Long materialId = 1L;
        int orderedQuantity = 10;

        materialOrderService.createMaterialOrder(type, materialId, orderedQuantity, materialsForOrder);

        assertEquals(1, materialsForOrder.size());
        MaterialOrderItemDTO createdItem = materialsForOrder.get(0);
        assertEquals(type, createdItem.getMaterialType());
        assertEquals(materialId, createdItem.getMaterialId());
        assertEquals(-orderedQuantity, createdItem.getOrderedQuantity()); // Check if it was negated
    }
    @Test
    void testAllOrderedProducts() {
        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setId(1L);
        orderProduct1.setNumber(10);
        Package package1 = new Package();
        package1.setId(1L);
        orderProduct1.setPackageId(package1);
        Order order1 = new Order();
        order1.setId(1L);
        orderProduct1.setOrderId(order1);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setId(2L);
        orderProduct2.setNumber(5);
        Package package2 = new Package();
        package2.setId(2L);
        orderProduct2.setPackageId(package2);
        Order order2 = new Order();
        order2.setId(2L);
        orderProduct2.setOrderId(order2);

        List<OrderProduct> mockProducts = Arrays.asList(orderProduct1, orderProduct2);

        when(orderProductRepository.findAll()).thenReturn(mockProducts);

        List<MaterialOrderItemDTO> result = materialOrderService.allOrderedProducts();

        assertNotNull(result);
        assertEquals(2, result.size()); // We expect two material ordered products created
        assertEquals(orderProduct1.getNumber(), result.get(0).getOrderedQuantity());
        assertEquals(orderProduct2.getNumber(), result.get(1).getOrderedQuantity());
    }
    @Test
    void testFindPackageByMaterialId_WithExistingPackage() {
        long materialId = 1L;
        Package packageEntity = new Package();
        when(packageRepository.findByIdAndDeletedFalse(materialId)).thenReturn(Optional.of(packageEntity));

        Package result = materialOrderService.findPackageByMaterialId(materialId);

        assertNotNull(result);
        assertEquals(packageEntity, result);
    }

//    @Test
//    void testFindPackageByMaterialId_WithNonExistingPackage() {
//        long materialId = 1L



//
//    @InjectMocks
//    private MaterialOrderService materialOrderService;
//    @Mock
//    private OrderProductRepository orderProductRepository;
//
//    @Mock
//    private PackageRepository packageRepository;
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private MaterialOrderRepository materialOrderRepository;
//    @Mock
//    private CartonRepository cartonRepository;
//
//    @Mock
//    private PlateRepository plateRepository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAllMaterialOrders() {
//        // Mocking the MaterialOrder objects
//        MaterialOrder order1 = new MaterialOrder(1L, 10, 5, 1L, MaterialType.CARTON, BigDecimal.valueOf(10), LocalDate.now(), false);
//        MaterialOrder order2 = new MaterialOrder(2L, 20, 15, 2L, MaterialType.PACKAGE, BigDecimal.valueOf(20), LocalDate.now(), false);
//        List<MaterialOrder> materialOrders = Arrays.asList(order1, order2);
//
//        // Mocking the behavior of the repository
//        when(materialOrderRepository.findByDeletedFalse()).thenReturn(materialOrders);
//
//        // Mocking the behavior of the ModelMapper
//        MaterialOrderDTO dto1 = new MaterialOrderDTO(1L, 10, 5, 1L, "CARTON", BigDecimal.valueOf(10), LocalDate.now());
//        MaterialOrderDTO dto2 = new MaterialOrderDTO(2L, 20, 15, 2L, "PACKAGE", BigDecimal.valueOf(20), LocalDate.now());
//        when(modelMapper.map(order1, MaterialOrderDTO.class)).thenReturn(dto1);
//        when(modelMapper.map(order2, MaterialOrderDTO.class)).thenReturn(dto2);
//
//        // Call the service method
//        List<MaterialOrderDTO> result = materialOrderService.getAllMaterialOrders();
//
//        // Verify the results
//        assertEquals(2, result.size());
//        assertEquals(dto1, result.get(0));
//        assertEquals(dto2, result.get(1));
//    }
//
//    @Test
//    void testGetMaterialOrderById_ValidId_ReturnsMaterialOrderDTO() throws ChangeSetPersister.NotFoundException {
//        // Mocking the MaterialOrder object
//        MaterialOrder materialOrder = new MaterialOrder(1L, 10, 5, 1L, MaterialType.CARTON, BigDecimal.valueOf(10), LocalDate.now(), false);
//
//        // Mocking the behavior of the repository
//        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(materialOrder));
//
//        // Mocking the behavior of the ModelMapper
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO(1L, 10, 5, 1L, "CARTON", BigDecimal.valueOf(10), LocalDate.now());
//        when(modelMapper.map(materialOrder, MaterialOrderDTO.class)).thenReturn(materialOrderDTO);
//
//        // Call the service method
//        MaterialOrderDTO result = materialOrderService.getMaterialOrderById(1L);
//
//        // Verify the result
//        assertEquals(materialOrderDTO, result);
//    }
//
//    @Test
//    void testGetMaterialOrderById_InvalidId_ThrowsNotFoundException() {
//        // Mocking the behavior of the repository when an ID is not found
//        when(materialOrderRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());
//
//        // Call the service method with an invalid ID and expect an exception
//        assertThrows(ChangeSetPersister.NotFoundException.class, () -> materialOrderService.getMaterialOrderById(999L));
//    }
//
//    @Test
//    void testValidate_ValidMaterialOrderDTO_NoExceptionThrown() {
//        // Create a valid MaterialOrderDTO
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById methods to return true for valid IDs
//        when(cartonRepository.existsById(1L)).thenReturn(true);
//
//        // Call the validate method and expect no exception to be thrown
//        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_MissingMaterialId_ThrowsValidationException() {
//        // Create a MaterialOrderDTO with a null material ID
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_InvalidMaterialType_ThrowsValidationException() {
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("INVALID_TYPE");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_InvalidCartonId_ThrowsValidationException() {
//        // Create a MaterialOrderDTO with an invalid carton ID
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById method to return false for the carton ID
//        when(cartonRepository.existsById(1L)).thenReturn(false);
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//
//    @Test
//    void testValidate_ValidPackageMaterialOrderDTO_NoExceptionThrown() {
//        // Create a valid MaterialOrderDTO with material type "PACKAGE"
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("PACKAGE");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById method to return true for the package ID
//        when(packageRepository.existsById(1L)).thenReturn(true);
//
//        // Call the validate method and expect no exception to be thrown
//        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_ValidPlateMaterialOrderDTO_NoExceptionThrown() {
//        // Create a valid MaterialOrderDTO with material type "PLATE"
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("PLATE");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById method to return true for the plate ID
//        when(plateRepository.existsById(1L)).thenReturn(true);
//
//        // Call the validate method and expect no exception to be thrown
//        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_InvalidPackageId_ThrowsValidationException() {
//        // Create a MaterialOrderDTO with an invalid package ID
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("PACKAGE");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById method to return false for the package ID
//        when(packageRepository.existsById(1L)).thenReturn(false);
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_InvalidPlateId_ThrowsValidationException() {
//        // Create a MaterialOrderDTO with an invalid plate ID
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("PLATE");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the existsById method to return false for the plate ID
//        when(plateRepository.existsById(1L)).thenReturn(false);
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//    @Test
//    void testValidate_NonPositiveOrderedQuantity_ThrowsValidationException() {
//        // Create a MaterialOrderDTO with non-positive ordered quantity
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(0); // <= 0
//
//        // Call the validate method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//    }
//
//
//    @Test
//    void testDeleteMaterialOrderDeleted() throws ChangeSetPersister.NotFoundException {
//        // Create a MaterialOrder with the provided ID
//        long orderIdToDelete = 1L;
//        MaterialOrder materialOrderToDelete = new MaterialOrder();
//        materialOrderToDelete.setId(orderIdToDelete);
//        materialOrderToDelete.setDeleted(false);
//
//        // Mock the behavior of the repository to return the MaterialOrder when findByIdAndDeletedFalse is called
//        when(materialOrderRepository.findByIdAndDeletedFalse(orderIdToDelete)).thenReturn(java.util.Optional.of(materialOrderToDelete));
//
//        // Call the deleteMaterialOrder method
//        materialOrderService.deleteMaterialOrder(orderIdToDelete);
//
//        // Verify that the materialOrderRepository.save method is called with the materialOrderToDelete and that its 'deleted' field is set to true
//        verify(materialOrderRepository).save(materialOrderToDelete);
//
//        // Verify that the materialOrderToDelete is set to deleted (true) after calling deleteMaterialOrder
//        assertTrue(materialOrderToDelete.isDeleted());
//    }
//
//    @Test
//    void testDeleteMaterialOrder_InvalidId_ThrowsNotFoundException() {
//        // Create an invalid ID that doesn't exist in the repository
//        long invalidOrderId = 999L;
//
//        // Mock the behavior of the repository to return an empty Optional when findByIdAndDeletedFalse is called with the invalid ID
//        when(materialOrderRepository.findByIdAndDeletedFalse(invalidOrderId)).thenReturn(java.util.Optional.empty());
//
//        // Call the deleteMaterialOrder method with the invalid ID and expect a NotFoundException
//        assertThrows(ChangeSetPersister.NotFoundException.class, () -> materialOrderService.deleteMaterialOrder(invalidOrderId));
//
//    }
//
//
//    @Test
//    void testCreateMaterialOrder_ThrowsValidationException() {
//        // Create a valid MaterialOrderDTO
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the behavior of the cartonRepository.existsById method to return false for the invalid Carton ID
//        when(cartonRepository.existsById(1L)).thenReturn(false);
//
//        // Call the createMaterialOrder method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.createMaterialOrder(materialOrderDTO));
//
//        // Verify that the cartonRepository.existsById method is called with the provided Carton ID
//        verify(cartonRepository).existsById(1L);
//
//        // Verify that the materialOrderRepository.save method is not called since the validation should fail before saving
//        verify(materialOrderRepository, never()).save(any(MaterialOrder.class));
//    }
//
//    @Test
//    void testCreateMaterialOrder_InvalidCartonId_ThrowsValidationException() {
//        // Create a valid MaterialOrderDTO
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the behavior of the cartonRepository.existsById method to return false for the invalid Carton ID
//        when(cartonRepository.existsById(1L)).thenReturn(false);
//
//        // Call the createMaterialOrder method and expect a ValidationException to be thrown
//        assertThrows(ValidationException.class, () -> materialOrderService.createMaterialOrder(materialOrderDTO));
//
//        // Verify that the cartonRepository.existsById method is called with the provided Carton ID
//        verify(cartonRepository).existsById(1L);
//
//        // Verify that the materialOrderRepository.save method is not called since the validation should fail before saving
//        verify(materialOrderRepository, never()).save(any(MaterialOrder.class));
//    }
//
//    @Test
//    void testUpdateMaterialOrder_ValidMaterialOrderDTO_UpdatesAndReturnsUpdatedMaterialOrderDTO() throws ChangeSetPersister.NotFoundException {
//        Long existingMaterialOrderId = 1L;
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(2L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(5);
//        materialOrderDTO.setReceivedQuantity(5);
//
//        MaterialOrder existingMaterialOrder = new MaterialOrder();
//        existingMaterialOrder.setId(existingMaterialOrderId);
//        existingMaterialOrder.setMaterialId(1L);
//        existingMaterialOrder.setMaterialType(MaterialType.CARTON);
//        existingMaterialOrder.setReceivedQuantity(2);
//        when(materialOrderRepository.findByIdAndDeletedFalse(existingMaterialOrderId)).thenReturn(Optional.of(existingMaterialOrder));
//
//        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        Carton carton = new Carton();
//        carton.setId(materialOrderDTO.getMaterialId());
//        carton.setAvailableQuantity(10);
//        when(cartonRepository.findById(materialOrderDTO.getMaterialId())).thenReturn(Optional.of(carton));
//
//        MaterialOrder updatedMaterialOrder = new MaterialOrder();
//        updatedMaterialOrder.setId(existingMaterialOrderId);
//        updatedMaterialOrder.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrder.setMaterialType(MaterialType.CARTON);
//        updatedMaterialOrder.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(updatedMaterialOrder);
//
//        MaterialOrderDTO updatedMaterialOrderDTO = new MaterialOrderDTO();
//        updatedMaterialOrderDTO.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrderDTO.setMaterialType(MaterialType.CARTON.name());
//        updatedMaterialOrderDTO.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class)).thenReturn(updatedMaterialOrderDTO);
//
//        MaterialOrderDTO result = materialOrderService.updateMaterialOrder(existingMaterialOrderId, materialOrderDTO);
//
//        verify(materialOrderRepository).findByIdAndDeletedFalse(existingMaterialOrderId);
//
//        verify(cartonRepository).existsById(materialOrderDTO.getMaterialId());
//
//        verify(materialOrderRepository).save(updatedMaterialOrder);
//
//        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);
//
//        verify(modelMapper).map(updatedMaterialOrder, MaterialOrderDTO.class);
//
//        assertEquals(materialOrderDTO.getMaterialId(), result.getMaterialId());
//        assertEquals(materialOrderDTO.getReceivedQuantity(), result.getReceivedQuantity());
//    }
//
//    @Test
//    void testUpdateMaterialOrder_ValidMaterialOrderDTO_UpdatesAndReturnsUpdatedMaterialOrderDTOPackage() throws ChangeSetPersister.NotFoundException {
//        Long existingMaterialOrderId = 1L;
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(2L);
//        materialOrderDTO.setMaterialType("PACKAGE");
//        materialOrderDTO.setOrderedQuantity(5);
//        materialOrderDTO.setReceivedQuantity(5);
//
//        MaterialOrder existingMaterialOrder = new MaterialOrder();
//        existingMaterialOrder.setId(existingMaterialOrderId);
//        existingMaterialOrder.setMaterialId(1L);
//        existingMaterialOrder.setMaterialType(MaterialType.PACKAGE);
//        existingMaterialOrder.setReceivedQuantity(2);
//        when(materialOrderRepository.findByIdAndDeletedFalse(existingMaterialOrderId)).thenReturn(Optional.of(existingMaterialOrder));
//
//        when(packageRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        Package aPackage = new Package();
//        aPackage.setId(materialOrderDTO.getMaterialId());
//        aPackage.setAvailableQuantity(10);
//        when(packageRepository.findById(materialOrderDTO.getMaterialId())).thenReturn(Optional.of(aPackage));
//
//        MaterialOrder updatedMaterialOrder = new MaterialOrder();
//        updatedMaterialOrder.setId(existingMaterialOrderId);
//        updatedMaterialOrder.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrder.setMaterialType(MaterialType.PACKAGE);
//        updatedMaterialOrder.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(updatedMaterialOrder);
//
//        MaterialOrderDTO updatedMaterialOrderDTO = new MaterialOrderDTO();
//        updatedMaterialOrderDTO.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrderDTO.setMaterialType(MaterialType.PACKAGE.name());
//        updatedMaterialOrderDTO.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class)).thenReturn(updatedMaterialOrderDTO);
//
//        MaterialOrderDTO result = materialOrderService.updateMaterialOrder(existingMaterialOrderId, materialOrderDTO);
//
//        verify(materialOrderRepository).findByIdAndDeletedFalse(existingMaterialOrderId);
//
//        verify(packageRepository).existsById(materialOrderDTO.getMaterialId());
//
//        verify(materialOrderRepository).save(updatedMaterialOrder);
//
//        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);
//
//        verify(modelMapper).map(updatedMaterialOrder, MaterialOrderDTO.class);
//
//        assertEquals(materialOrderDTO.getMaterialId(), result.getMaterialId());
//        assertEquals(materialOrderDTO.getReceivedQuantity(), result.getReceivedQuantity());
//    }
//
//    @Test
//    void testUpdateMaterialOrder_ValidMaterialOrderDTO_UpdatesAndReturnsUpdatedMaterialOrderDTOPlate() throws ChangeSetPersister.NotFoundException {
//        Long existingMaterialOrderId = 1L;
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(2L);
//        materialOrderDTO.setMaterialType("PLATE");
//        materialOrderDTO.setOrderedQuantity(5);
//        materialOrderDTO.setReceivedQuantity(5);
//
//        MaterialOrder existingMaterialOrder = new MaterialOrder();
//        existingMaterialOrder.setId(existingMaterialOrderId);
//        existingMaterialOrder.setMaterialId(1L);
//        existingMaterialOrder.setMaterialType(MaterialType.PLATE);
//        existingMaterialOrder.setReceivedQuantity(2);
//        when(materialOrderRepository.findByIdAndDeletedFalse(existingMaterialOrderId)).thenReturn(Optional.of(existingMaterialOrder));
//
//        when(plateRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        Plate plate = new Plate();
//        plate.setId(materialOrderDTO.getMaterialId());
//        plate.setAvailableQuantity(10);
//        when(plateRepository.findById(materialOrderDTO.getMaterialId())).thenReturn(Optional.of(plate));
//
//        MaterialOrder updatedMaterialOrder = new MaterialOrder();
//        updatedMaterialOrder.setId(existingMaterialOrderId);
//        updatedMaterialOrder.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrder.setMaterialType(MaterialType.PLATE);
//        updatedMaterialOrder.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(updatedMaterialOrder);
//
//        MaterialOrderDTO updatedMaterialOrderDTO = new MaterialOrderDTO();
//        updatedMaterialOrderDTO.setMaterialId(materialOrderDTO.getMaterialId());
//        updatedMaterialOrderDTO.setMaterialType(MaterialType.PLATE.name());
//        updatedMaterialOrderDTO.setReceivedQuantity(materialOrderDTO.getReceivedQuantity());
//        when(modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class)).thenReturn(updatedMaterialOrderDTO);
//
//        MaterialOrderDTO result = materialOrderService.updateMaterialOrder(existingMaterialOrderId, materialOrderDTO);
//        verify(materialOrderRepository).findByIdAndDeletedFalse(existingMaterialOrderId);
//        verify(plateRepository).existsById(materialOrderDTO.getMaterialId());
//        verify(materialOrderRepository).save(updatedMaterialOrder);
//        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);
//        verify(modelMapper).map(updatedMaterialOrder, MaterialOrderDTO.class);
//
//        assertEquals(materialOrderDTO.getMaterialId(), result.getMaterialId());
//        assertEquals(materialOrderDTO.getReceivedQuantity(), result.getReceivedQuantity());
//    }
//
//    @Test
//    void testCreateMaterialOrder_ValidMaterialOrderDTO_CreatesAndReturnsMaterialOrderDTO() {
//        // Create a valid MaterialOrderDTO
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L); // Assuming we have a valid Carton ID
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the behavior of the cartonRepository.existsById method to return true for the valid Carton ID
//        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        // Mock the behavior of the modelMapper to return a MaterialOrder when mapping from DTO to entity
//        MaterialOrder materialOrderEntity = new MaterialOrder();
//        materialOrderEntity.setId(1L);
//        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(materialOrderEntity);
//
//        // Mock the behavior of the materialOrderRepository to return the created MaterialOrder
//        MaterialOrder createdMaterialOrder = new MaterialOrder();
//        createdMaterialOrder.setId(1L);
//        when(materialOrderRepository.save(any(MaterialOrder.class))).thenReturn(createdMaterialOrder);
//
//        // Call the createMaterialOrder method
//        MaterialOrderDTO result = materialOrderService.createMaterialOrder(materialOrderDTO);
//
//        // Verify that the cartonRepository.existsById method is called with the Carton ID from the MaterialOrderDTO
//        verify(cartonRepository).existsById(materialOrderDTO.getMaterialId());
//
//        // Verify that the modelMapper.map method is called with the input DTO
//        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);
//
//        // Verify that the materialOrderRepository.save method is called with the created entity
//        verify(materialOrderRepository).save(materialOrderEntity);
//
//        // Verify that the returned MaterialOrderDTO contains the correct ID
//        assertEquals(createdMaterialOrder.getId(), result.getId());
//    }
//
//    @Test
//    void testValidate_ValidMaterialOrderDTO_DoesNotThrowException() {
//        // Create a valid MaterialOrderDTO
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // Mock the behavior of the cartonRepository.existsById method to return true for the valid Carton ID
//        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        // Call the validate method
//        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
//    }
//
//
//    @Test
//    void testValidate_NegativeOrderedQuantity_ThrowsValidationException() {
//        // Create an invalid MaterialOrderDTO with a negative ordered quantity
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("CARTON");
//        materialOrderDTO.setOrderedQuantity(-5);
//
//        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        // Call the validate method and expect ValidationException with specific message
//        ValidationException exception = assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//        assertEquals("Ordered Quantity must be greater than zero", exception.getMessage());
//    }
//
//    @Test
//    void testValidate_NegativeOrderedQuantityy_ThrowsValidationException() {
//        // Create an invalid MaterialOrderDTO with a negative ordered quantity
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("INVALIt");
//        materialOrderDTO.setOrderedQuantity(1);
//
//        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);
//
//        // Call the validate method and expect ValidationException with specific message
//        ValidationException exception = assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//        assertEquals("Invalid Material Type", exception.getMessage());
//    }
//
//    @Test
//    void testCalculateCartonInsufficientNumbers() {
//        // Създайте обект от класа Package, който да бъде подаден на тествания метод
//        Package packageEntity = new Package();
//        packageEntity.setAvailableQuantity(10); // Поставете желаната стойност за тестване
//        packageEntity.setPiecesCarton(2); // Поставете желаната стойност за тестване
//
//        // Създайте обект от класа Carton и го свържете с packageEntity
//        Carton carton = new Carton();
//        carton.setAvailableQuantity(5); // Поставете желаната стойност за тестване
//        packageEntity.setCartonId(carton);
//
//        // Извикайте тествания метод
//        int result = materialOrderService.calculateCartonInsufficientNumbers(packageEntity);
//
//        // Проверете резултата
//        assertEquals(10, result); // Очакваме резултатът да е 20
//    }
//
//    @Test
//    void testCalculatePlateInsufficientNumbers() {
//        // Създайте обект от класа Package, който да бъде подаден на тествания метод
//        Package packageEntity = new Package();
//        packageEntity.setPlateId(new Plate()); // Поставете желаната стойност за тестване
//        packageEntity.getPlateId().setAvailableQuantity(5); // Поставете желаната стойност за тестване
//
//        // Извикайте тествания метод
//        int result = materialOrderService.calculatePlateInsufficientNumbers(packageEntity);
//
//        // Проверете резултата
//        assertEquals(5, result); // Очакваме резултатът да е 5
//    }
//
//    @Test
//    void testCalculatePackageInsufficientNumbers() {
//        // Създайте обект от класа Package, който да бъде подаден на тествания метод
//        Package packageEntity = new Package();
//        packageEntity.setAvailableQuantity(100); // Поставете желаната стойност за тестване
//
//        // Извикайте тествания метод
//        int result = materialOrderService.calculatePackageInsufficientNumbers(packageEntity);
//
//        // Проверете резултата
//        assertEquals(100, result); // Очакваме резултатът да е 100
//    }
//
//    @Test
//    void validate_InvalidMaterialType_ThrowsValidationException_DefaultCase() {
//        // Given
//        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
//        materialOrderDTO.setMaterialId(1L);
//        materialOrderDTO.setMaterialType("INVALID");
//        materialOrderDTO.setOrderedQuantity(10);
//
//        // When & Then
//        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
//
//        // Verify that neither cartonRepository.existsById nor packageRepository.existsById nor plateRepository.existsById
//        // was called for the default case in the switch statement
//        verify(cartonRepository, never()).existsById(anyLong());
//        verify(packageRepository, never()).existsById(anyLong());
//        verify(plateRepository, never()).existsById(anyLong());
//    }
//
//
////    @Test
////    void testGetAllOrderProducts2ByOrderId() {
////        // Подгответе мокнати данни, които ще върне orderProductRepository.findAll()
////        Order order = new Order();
////        order.setId(1L);
////
////        Package packageEntity = mock(Package.class);
////        when(packageEntity.getAvailableQuantity()).thenReturn(100); // Примерно връща 100 налични кашони
////        packageEntity.setPiecesCarton(10); // Настройте стойността на piecesCarton на 10
////
////        Carton carton = mock(Carton.class);
////        when(carton.getAvailableQuantity()).thenReturn(100); // Примерно връща 100 налични кашони
////        packageEntity.setCartonId(carton);
////
////        Plate plate = mock(Plate.class);
////        when(plate.getAvailableQuantity()).thenReturn(50); // Примерно връща 50 налични тарелки
////        packageEntity.setPlateId(plate);
////
////        List<OrderProduct> orderProducts = Arrays.asList(
////                new OrderProduct(1L, 10, order, packageEntity, false),
////                new OrderProduct(2L, 10, order, packageEntity, false),
////                new OrderProduct(1L, 10, order, packageEntity, false)
////        );
////        when(orderProductRepository.findAll()).thenReturn(orderProducts);
////
////        // Проверка за хвърляне на изключение
////        assertThrows(NullPointerException.class, () -> materialOrderService.getAllOrderProductsByOrderId(1L), "Invalid Package ID");
////    }
//
//    @Test
//    void testIncreaseProductsQuantityForCarton() {
//        // Create a test MaterialOrder for a CARTON
//        MaterialOrder cartonOrder = new MaterialOrder();
//        cartonOrder.setMaterialType(MaterialType.CARTON);
//        cartonOrder.setMaterialId(1L); // Replace with the appropriate ID
//        cartonOrder.setReceivedQuantity(10);
//
//        // Mock the Carton object that will be returned by the repository
//        Carton carton = new Carton();
//        carton.setAvailableQuantity(5); // Set the initial available quantity
//
//        // Mock the repository's findById method to return the mocked Carton
//        when(cartonRepository.findById(cartonOrder.getMaterialId())).thenReturn(Optional.of(carton));
//
//        // Call the method to be tested
//        materialOrderService.increaseProductsQuantity(cartonOrder);
//
//        // Verify that the available quantity is increased as expected
//        assertEquals(15, carton.getAvailableQuantity());
//    }
//
//    @Test
//    void testIncreaseProductsQuantityForPlate() {
//        MaterialOrder plateOrder = new MaterialOrder();
//        plateOrder.setMaterialType(MaterialType.PLATE);
//        plateOrder.setMaterialId(1L);
//        plateOrder.setReceivedQuantity(10);
//
//        Plate plate = new Plate();
//        plate.setAvailableQuantity(5);
//
//        when(plateRepository.findById(plateOrder.getMaterialId())).thenReturn(Optional.of(plate));
//
//        materialOrderService.increaseProductsQuantity(plateOrder);
//
//        assertEquals(15, plate.getAvailableQuantity());
//    }
//
//    @Test
//    void testIncreaseProductsQuantityForPackage() {
//        MaterialOrder packageOrder = new MaterialOrder();
//        packageOrder.setMaterialType(MaterialType.PACKAGE);
//        packageOrder.setMaterialId(1L);
//        packageOrder.setReceivedQuantity(10);
//
//        Package aPackage = new Package();
//        aPackage.setAvailableQuantity(5);
//
//        when(packageRepository.findById(packageOrder.getMaterialId())).thenReturn(Optional.of(aPackage));
//
//        materialOrderService.increaseProductsQuantity(packageOrder);
//
//        assertEquals(15, aPackage.getAvailableQuantity());
//    }
//
//    @Test
//    void testAllOrderedProducts() {
//        Package entityPackage = new Package();
//        entityPackage.setId(1L);
//
//        List<OrderProduct> orderProducts = new ArrayList<>();
//        OrderProduct orderProduct1 = new OrderProduct();
//        orderProduct1.setPackageId(entityPackage);
//        orderProduct1.setNumber(5);
//        Order order=new Order();
//        order.setInvoiced(false);
//        orderProduct1.setOrderId(order);
//
//        orderProducts.add(orderProduct1);
//
//
//        when(orderProductRepository.findAll()).thenReturn(orderProducts);
//
//        List<MaterialOrderDTO> result = materialOrderService.allOrderedProducts();
//
//        assertEquals(1, result.size());
//
//        assertEquals(1L, result.get(0).getMaterialId());
//        assertEquals(5, result.get(0).getOrderedQuantity());
//
//    }
//
//    @Test
//    void testCreateMaterialOrder() {
//        // Arrange
//        MaterialType materialType = MaterialType.PACKAGE;
//        Long materialId = 123L;
//        int orderedQuantity = 10;
//        List<MaterialOrderDTO> materialsForOrder = new ArrayList<>();
//
//        // Act
//        materialOrderService.createMaterialOrder(materialType, materialId, orderedQuantity, materialsForOrder);
//
//        // Assert
//        assertEquals(1, materialsForOrder.size());
//
//        MaterialOrderDTO materialOrderDTO = materialsForOrder.get(0);
//        assertEquals(materialId, materialOrderDTO.getMaterialId());
//        assertEquals(materialType.toString(), materialOrderDTO.getMaterialType());
//        assertEquals(-1 * orderedQuantity, materialOrderDTO.getOrderedQuantity());
//    }
//
//    @Test
//    void testCreateMaterialOrder2() {
//        // Arrange
//        MaterialType materialType = MaterialType.PACKAGE;
//        Long materialId = 123L;
//        int orderedQuantity = 10;
//        List<MaterialOrderDTO> materialsForOrder = new ArrayList<>();
//
//        // Act
//        materialOrderService.createMaterialOrder(materialType, materialId, orderedQuantity, materialsForOrder);
//
//        // Assert
//        assertEquals(1, materialsForOrder.size());
//
//        MaterialOrderDTO materialOrderDTO = materialsForOrder.get(0);
//        assertEquals(materialId, materialOrderDTO.getMaterialId());
//        assertEquals(materialType.toString(), materialOrderDTO.getMaterialType());
//        assertEquals(-1 * orderedQuantity, materialOrderDTO.getOrderedQuantity());
//    }
//
//    @Test
//    void testAllMissingMaterials() {
//        // Arrange
//        List<MaterialOrderDTO> allNeedsMaterialOrders = new ArrayList<>();
//        // Add MaterialOrderDTO objects to allNeedsMaterialOrders as needed for the test
//
//        // Prepare mock data
//        Long materialId = 123L;
//        int orderedQuantity = 10;
//        Package packageEntity = new Package();
//        packageEntity.setPlateId(new Plate());
//        packageEntity.setCartonId(new Carton());
//        packageEntity.setPiecesCarton(5);
//        Product product = new Product();
//        product.setAvailableQuantity(5);
//
//        when(packageRepository.findByIdAndDeletedFalse(materialId)).thenReturn(Optional.of(packageEntity));
//        when(productRepository.findByIdAndDeletedFalse(packageEntity.getId())).thenReturn(Optional.of(product));
//
//        // Act
//        List<MaterialOrderDTO> result = materialOrderService.allMissingMaterials(allNeedsMaterialOrders);
//
//        assertEquals(0, result.size());
//    }
//
//
//    @Test
//    void testCreateMaterialOrder_AddsToMaterialsForOrderList() {
//        // Arrange
//        MaterialType materialType = MaterialType.PLATE;
//        Long materialId = 123L;
//        int orderedQuantity = 10;
//        List<MaterialOrderDTO> materialsForOrder = new ArrayList<>();
//
//        // Act
//        materialOrderService.createMaterialOrder(materialType, materialId, orderedQuantity, materialsForOrder);
//
//        // Assert
//        assertEquals(1, materialsForOrder.size());
//
//        MaterialOrderDTO addedMaterialOrder = materialsForOrder.get(0);
//        assertEquals(materialId, addedMaterialOrder.getMaterialId());
//        assertEquals(materialType.toString(), addedMaterialOrder.getMaterialType());
//        assertEquals(-1 * orderedQuantity, addedMaterialOrder.getOrderedQuantity());
//    }
//
//    @Test
//    void testCalculatePlateInsufficientNumbers_WithAvailableQuantity() {
//        // Arrange
//        Package packageEntity = new Package();
//        Plate plate = new Plate();
//        plate.setAvailableQuantity(12);
//        packageEntity.setPlateId(plate);
//
//        // Act
//        int result = materialOrderService.calculatePlateInsufficientNumbers(packageEntity);
//
//        // Assert
//        assertEquals(12, result); // Expecting the available quantity of the plate
//    }
//
//    @Test
//    void testCalculatePlateInsufficientNumbers_WithoutAvailableQuantity() {
//        // Arrange
//        Package packageEntity = new Package();
//        Plate plate = new Plate();
//        packageEntity.setPlateId(plate);
//
//        // Act and Assert
//        assertThrows(NullPointerException.class, () -> materialOrderService.calculatePlateInsufficientNumbers(packageEntity));
//        // Expecting a NullPointerException because there's no available quantity for the plate
//    }
//
//    @Test
//    void testAllMissingMaterials_PackageInsufficient3() {
//        // Arrange
//        List<MaterialOrderDTO> allNeedsMaterialOrders = new ArrayList<>();
//        // Add MaterialOrderDTO objects to allNeedsMaterialOrders as needed for the test
//
//        // Prepare mock data for Package
//        Long packageId = 1L;
//        Carton carton = new Carton();
//        carton.setAvailableQuantity(10);
//        Plate plate = new Plate();
//        plate.setAvailableQuantity(10000);
//        Package packageEntity = new Package();
//        packageEntity.setId(packageId);
//        packageEntity.setPiecesCarton(2);
//        packageEntity.setCartonId(carton);
//        packageEntity.setPlateId(plate);
//        packageEntity.setAvailableQuantity(20);
//        // Set other properties of packageEntity as needed for the test
//
//        // Prepare mock data for Product
//        Product product = new Product();
//        product.setPackageId(packageEntity);
//        product.setAvailableQuantity(15);
//
//        // Mocking repository calls
//        when(packageRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(packageEntity));
//        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(product));
//
//        // Act
//        List<MaterialOrderDTO> result = materialOrderService.allMissingMaterials(allNeedsMaterialOrders);
//
//        // Assert
//        assertEquals(0, result.size()); // Check that the result contains exactly 1 element
//
//    }
//
////    @Test
////    void testFindPackageByMaterialId() {
////        // Подготовка на данни: предполагаме, че имаме пакет с даден материален идентификатор (например 123)
////        long materialId = 123;
////        Package packageEntity = new Package();
////        packageEntity.setId(materialId);
////    }
//
//    @Test
//    void testGetAllOrderProductsByOrderId() {
//        Long orderId = 123L;
//
//        // Create a list of mock OrderProduct objects
//        List<OrderProduct> mockOrderProducts = new ArrayList<>();
//        OrderProduct orderProduct = new OrderProduct();
//        orderProduct.setId(1L);
//        Order order = new Order();
//        order.setId(1L);
//        orderProduct.setOrderId(order);
//        mockOrderProducts.add(orderProduct);
//        mockOrderProducts.add(orderProduct);
//        mockOrderProducts.add(orderProduct);
//        // Mock the findAll() method of the orderProductRepository to return the mockOrderProducts list
//        when(orderProductRepository.findAll()).thenReturn(mockOrderProducts);
//
//        // Call the method under test
//        List<MaterialOrderDTO> result = materialOrderService.getAllOrderProductsByOrderId(orderId);
//
//        // Verify the expected result
//        Assertions.assertEquals(0, result.size()); // Expecting 2 OrderProduct objects with matching orderId
//    }
//
//    @Test
//    void testFindPackageByMaterialId() {
//        // Подготовка на данни: предполагаме, че имаме пакет с даден материален идентификатор (например 123)
//        long materialId = 123;
//        Package packageEntity = new Package();
//        packageEntity.setId(materialId);
//
//
//        // Конфигурираме мока на packageRepository.findByIdAndDeletedFalse да връща Optional с пакета
//        when(packageRepository.findByIdAndDeletedFalse(materialId)).thenReturn(Optional.of(packageEntity));
//
//        // Извикваме метода, който тестваме
//        Package result = materialOrderService.findPackageByMaterialId(materialId);
//
//        // Проверяваме дали резултатът е същият като пакета, който очакваме да бъде върнат от мока
//        assertEquals(packageEntity, result);
//    }
//
//    @Test
//    void testFindPackageByMaterialIdNotFound() {
//        // Подготовка на данни: предполагаме, че не съществува пакет с даден материален идентификатор (например 456)
//        long materialId = 456;
//
//        // Конфигурираме мока на packageRepository.findByIdAndDeletedFalse да връща празен Optional (пакетът не съществува)
//        when(packageRepository.findByIdAndDeletedFalse(materialId)).thenReturn(Optional.empty());
//
//        // Извикваме метода, който тестваме
//        Package result = materialOrderService.findPackageByMaterialId(materialId);
//
//        // Проверяваме дали резултатът е null, тъй като пакетът не съществува
//        assertEquals(null, result);
//    }
//
//    @Test
//    void testGetProductFromPackage() {
//        long packageId = 123;
//        Package packageEntity = new Package();
//        packageEntity.setId(packageId);
//
//        Product product = new Product();
//        product.setId(456L);
//        product.setDeleted(false);
//
//        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity)).thenReturn(Optional.of(product));
//
//        Product result = materialOrderService.getProductFromPackage(packageEntity);
//
//        assertEquals(product, result);
//    }
//
//    @Test
//    void testGetProductFromPackageNotFound() {
//        // Подготовка на данни: предполагаме, че не съществува пакет с даден идентификатор (например 789)
//        long packageId = 789;
//        Package packageEntity = new Package();
//        packageEntity.setId(packageId);
//
//        // Конфигурираме мока на productRepository.findByIdAndDeletedFalse да връща празен Optional (продуктът не съществува)
//        when(productRepository.findByIdAndDeletedFalse(packageId)).thenReturn(Optional.empty());
//
//        // Очакваме RuntimeException със съобщението "Продуктът не беше намерен"
//        assertThrows(RuntimeException.class, () -> {
//            materialOrderService.getProductFromPackage(packageEntity);
//        });
//    }
//
//    @Test
//    void testCreatePackageInsufficientMaterialOrder() {
//        // Подготовка на данни: предполагаме, че имаме пакет с налични бройки (например 100)
//        int availableQuantity = 100;
//
//        // Подготовка на Package с валидни данни
//        Package packageEntity = new Package();
//        packageEntity.setAvailableQuantity(availableQuantity);
//
//        // Подготовка на MaterialOrderDTO с нужно количество поръчан материал
//        MaterialOrderDTO allNeedsMaterialOrder = new MaterialOrderDTO();
//        allNeedsMaterialOrder.setOrderedQuantity(50);
//
//        // Подготовка на списък с материални поръчки
//        List<MaterialOrderDTO> allMaterialsForAllOrders = new ArrayList<>();
//
//        // Извикваме метода, който тестваме
//        materialOrderService.createPackageInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
//
//        // Проверяваме дали е добавена материална поръчка към списъка с правилни стойности
//        assertEquals(0, allMaterialsForAllOrders.size()); // Очакваме 1 материална поръчка, тъй като недостигащите бройки са (100 - 50) = 50
//    }
//
//    @Test
//    void testCreatePackageInsufficientMaterialOrderNoOrderNeeded() {
//        // Подготовка на данни: предполагаме, че имаме пакет с достатъчно налични бройки (например 100)
//        int availableQuantity = 100;
//
//        // Подготовка на Package с валидни данни
//        Package packageEntity = new Package();
//        packageEntity.setAvailableQuantity(availableQuantity);
//
//        // Подготовка на MaterialOrderDTO с нулево количество поръчан материал
//        MaterialOrderDTO allNeedsMaterialOrder = new MaterialOrderDTO();
//        allNeedsMaterialOrder.setOrderedQuantity(0);
//        List<MaterialOrderDTO> allMaterialsForAllOrders = new ArrayList<>();
//        materialOrderService.createPackageInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
//        assertEquals(0, allMaterialsForAllOrders.size());
//    }
@Test
void testUpdateMaterialOrderItem_Success() {
    MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
    materialOrderItemDTO.setId(1L);
    materialOrderItemDTO.setMaterialType(MaterialType.CARTON);
    materialOrderItemDTO.setMaterialId(100L);
    materialOrderItemDTO.setReceivedQuantity(5);

    existingMaterialOrder = new MaterialOrder();
    existingMaterialOrder.setId(1L);

    MaterialOrderItem materialOrderItem = new MaterialOrderItem();
    materialOrderItem.setId(1L);
    materialOrderItem.setMaterialType(MaterialType.CARTON);
    materialOrderItem.setMaterialId(100L);
    materialOrderItem.setReceivedQuantity(5);
    when(materialOrderRepository.findByMaterialOrderItemId(1L))
            .thenReturn(Optional.of(existingMaterialOrder));
    when(materialOrderMapper.toItemEntity(materialOrderItemDTO, existingMaterialOrder))
            .thenReturn(materialOrderItem);
    Carton carton = new Carton();
    carton.setAvailableQuantity(10); // Ensure non-null value
    when(cartonRepository.findById(100L))
            .thenReturn(Optional.of(carton)); // Mock existing carton

    materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);

    verify(materialOrderItemRepository).save(materialOrderItem);
    verify(cartonRepository).save(any(Carton.class)); // Ensure carton quantity is updated
}

    @Test
    void testUpdateMaterialOrderItem_MaterialOrderNotFound() {
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.CARTON);
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);

        existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(1L);

        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setId(1L);
        materialOrderItem.setMaterialType(MaterialType.CARTON);
        materialOrderItem.setMaterialId(100L);
        materialOrderItem.setReceivedQuantity(5);
        when(materialOrderRepository.findByMaterialOrderItemId(1L))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);
        });
    }

    @Test
    void testUpdateMaterialOrderItem_CartonNotFound() {
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.CARTON);
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);

        existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(1L);

        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setId(1L);
        materialOrderItem.setMaterialType(MaterialType.CARTON);
        materialOrderItem.setMaterialId(100L);
        materialOrderItem.setReceivedQuantity(5);
        when(materialOrderRepository.findByMaterialOrderItemId(1L))
                .thenReturn(Optional.of(existingMaterialOrder));
        when(materialOrderMapper.toItemEntity(materialOrderItemDTO, existingMaterialOrder))
                .thenReturn(materialOrderItem);
        when(cartonRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);
        });
    }

    @Test
    void testUpdateMaterialOrderItem_PackageNotFound() {
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.PACKAGE);
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);

        // Create the existing MaterialOrderItem and add it to the order
        MaterialOrderItem existingItem = new MaterialOrderItem();
        existingItem.setId(1L);

        existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(1L);
        existingMaterialOrder.setItems(new ArrayList<>());
        existingMaterialOrder.getItems().add(existingItem); // Add the item to the order

        when(materialOrderRepository.findByMaterialOrderItemId(1L))
                .thenReturn(Optional.of(existingMaterialOrder));

//        when(packageRepository.findById(100L))
//                .thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> {
            materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);
        });
    }



    @Test
    void testUpdateMaterialOrderItem_PlateNotFound() {
        // Arrange
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.PLATE); // Set to PLATE initially
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);

        existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(1L);

        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setId(1L);
        materialOrderItem.setMaterialType(MaterialType.PLATE); // Ensure consistency
        materialOrderItem.setMaterialId(100L);
        materialOrderItem.setReceivedQuantity(5);

        when(materialOrderRepository.findByMaterialOrderItemId(1L))
                .thenReturn(Optional.of(existingMaterialOrder));
        when(materialOrderMapper.toItemEntity(materialOrderItemDTO, existingMaterialOrder))
                .thenReturn(materialOrderItem);

        // Mocking plateRepository to return empty (simulate "plate not found")
        when(plateRepository.findById(100L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);
        });
    }
    @Test
    void testGetAllMaterialOrderItems() {
        // Arrange
        MaterialOrderItem materialOrderItem1 = new MaterialOrderItem();
        materialOrderItem1.setId(1L);
        MaterialOrderItem materialOrderItem2 = new MaterialOrderItem();
        materialOrderItem2.setId(2L);

        List<MaterialOrderItem> materialOrderItems = List.of(materialOrderItem1, materialOrderItem2);

        when(materialOrderItemRepository.findAll()).thenReturn(materialOrderItems);
        when(modelMapper.map(any(MaterialOrderItem.class), eq(MaterialOrderItemDTO.class)))
                .thenAnswer(invocation -> {
                    MaterialOrderItem item = invocation.getArgument(0);
                    MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
                    dto.setId(item.getId());
                    return dto;
                });

        // Act
        List<MaterialOrderItemDTO> result = materialOrderService.getAllMaterialOrderItems();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(materialOrderItemRepository, times(1)).findAll();
    }

    @Test
    void testUpdateMaterialOrderItem_WhenMaterialTypeIsPackage_PackageExists() {
        // Arrange
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.PACKAGE);
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);

        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setId(1L);
        when(materialOrderRepository.findByMaterialOrderItemId(1L)).thenReturn(Optional.of(materialOrder));

        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setId(1L);
        materialOrderItem.setMaterialType(MaterialType.PACKAGE);
        materialOrderItem.setMaterialId(100L);
        materialOrderItem.setReceivedQuantity(5);

        // Mock the mapper to ensure it doesn't return null
        when(materialOrderMapper.toItemEntity(materialOrderItemDTO, materialOrder)).thenReturn(materialOrderItem);

        // Use doReturn instead of when().thenReturn()
        doReturn(materialOrderItem).when(materialOrderItemRepository).save(any(MaterialOrderItem.class));

        Package specificPackage = new Package();
        specificPackage.setId(100L);
        specificPackage.setAvailableQuantity(10);
        when(packageRepository.findById(100L)).thenReturn(Optional.of(specificPackage));

        // Act
        MaterialOrderItem result = materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(MaterialType.PACKAGE, result.getMaterialType());
        assertEquals(100L, result.getMaterialId());
        assertEquals(5, result.getReceivedQuantity());
        assertEquals(5, specificPackage.getAvailableQuantity());
        verify(packageRepository, times(1)).save(specificPackage);
        verify(materialOrderItemRepository, times(1)).save(any(MaterialOrderItem.class));
    }


    @Test
    void testUpdateMaterialOrderItem_WhenMaterialTypeIsPackage_PackageNotFound() {
        // Arrange
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.PACKAGE);
        materialOrderItemDTO.setMaterialId(100L);
        materialOrderItemDTO.setReceivedQuantity(5);
        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setMaterialType(MaterialType.valueOf("PACKAGE"));

        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setId(1L);
        lenient().when(materialOrderRepository.findByMaterialOrderItemId(anyLong())).thenReturn(Optional.of(materialOrder));

        lenient().when(packageRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(materialOrderMapper.toItemEntity(any(), any())).thenReturn(materialOrderItem);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> materialOrderService.updateMaterialOrderItem(materialOrderItemDTO));
    }
    @Test
    void testUpdateMaterialOrderItem_WhenMaterialTypeIsPlate_PlateExists() {
        // Arrange
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setId(1L);
        materialOrderItemDTO.setMaterialType(MaterialType.PLATE);
        materialOrderItemDTO.setMaterialId(200L);
        materialOrderItemDTO.setReceivedQuantity(3);

        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setId(1L);

        Plate plate = new Plate();
        plate.setId(200L);
        plate.setAvailableQuantity(10);

        MaterialOrderItem materialOrderItem = new MaterialOrderItem();
        materialOrderItem.setId(1L);
        materialOrderItem.setMaterialType(MaterialType.PLATE);
        materialOrderItem.setMaterialId(200L);
        materialOrderItem.setReceivedQuantity(3);

        lenient().when((materialOrderRepository).findByMaterialOrderItemId(anyLong())).thenReturn(Optional.of(materialOrder));
        lenient().when(materialOrderMapper.toItemEntity(any(), any())).thenReturn(materialOrderItem);
        lenient().when((plateRepository).findById(anyLong())).thenReturn(Optional.of(plate));
        lenient().when((materialOrderItemRepository).save(any(MaterialOrderItem.class))).thenReturn(materialOrderItem);

        // Act
        MaterialOrderItem result = materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(MaterialType.PLATE, result.getMaterialType());
        assertEquals(200L, result.getMaterialId());
        assertEquals(3, result.getReceivedQuantity());
        assertEquals(7, plate.getAvailableQuantity()); // 10 - 3 = 7
        verify(plateRepository, times(1)).save(plate);
    }
    @Test
    void testGetProductFromPackage_WhenProductExists() {
        // Arrange
        Package packageEntity = new Package();
        packageEntity.setId(100L);

        Product expectedProduct = new Product();
        expectedProduct.setId(200L);
        expectedProduct.setPackageId(packageEntity);

        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity))
                .thenReturn(Optional.of(expectedProduct));

        // Act
        Product result = materialOrderService.getProductFromPackage(packageEntity);

        // Assert
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals(packageEntity, result.getPackageId());
    }
    @Test
    void testGetProductFromPackage_WhenProductNotFound() {
        // Arrange
        Package packageEntity = new Package();
        packageEntity.setId(100L);

        when(productRepository.findByPackageIdAndDeletedFalse(packageEntity))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> materialOrderService.getProductFromPackage(packageEntity));

        assertEquals("Продуктът не беше намерен", exception.getMessage());
    }

}