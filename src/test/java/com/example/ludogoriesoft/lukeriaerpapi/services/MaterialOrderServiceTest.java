package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialOrderServiceTest {

    @InjectMocks
    private MaterialOrderService materialOrderService;

    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private OrderProductService orderProductService;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private MaterialOrderRepository materialOrderRepository;
    @Mock
    private CartonRepository cartonRepository;

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private ModelMapper modelMapper;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllMaterialOrders() {
        // Mocking the MaterialOrder objects
        MaterialOrder order1 = new MaterialOrder(1L, 10, 5, 1L, MaterialType.CARTON, BigDecimal.valueOf(10), LocalDate.now(), false);
        MaterialOrder order2 = new MaterialOrder(2L, 20, 15, 2L, MaterialType.PACKAGE, BigDecimal.valueOf(20), LocalDate.now(), false);
        List<MaterialOrder> materialOrders = Arrays.asList(order1, order2);

        // Mocking the behavior of the repository
        when(materialOrderRepository.findByDeletedFalse()).thenReturn(materialOrders);

        // Mocking the behavior of the ModelMapper
        MaterialOrderDTO dto1 = new MaterialOrderDTO(1L, 10, 5, 1L, "CARTON", BigDecimal.valueOf(10), LocalDate.now());
        MaterialOrderDTO dto2 = new MaterialOrderDTO(2L, 20, 15, 2L, "PACKAGE", BigDecimal.valueOf(20), LocalDate.now());
        when(modelMapper.map(order1, MaterialOrderDTO.class)).thenReturn(dto1);
        when(modelMapper.map(order2, MaterialOrderDTO.class)).thenReturn(dto2);

        // Call the service method
        List<MaterialOrderDTO> result = materialOrderService.getAllMaterialOrders();

        // Verify the results
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void testGetMaterialOrderById_ValidId_ReturnsMaterialOrderDTO() throws ChangeSetPersister.NotFoundException {
        // Mocking the MaterialOrder object
        MaterialOrder materialOrder = new MaterialOrder(1L, 10, 5, 1L, MaterialType.CARTON, BigDecimal.valueOf(10), LocalDate.now(), false);

        // Mocking the behavior of the repository
        when(materialOrderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(materialOrder));

        // Mocking the behavior of the ModelMapper
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO(1L, 10, 5, 1L, "CARTON", BigDecimal.valueOf(10), LocalDate.now());
        when(modelMapper.map(materialOrder, MaterialOrderDTO.class)).thenReturn(materialOrderDTO);

        // Call the service method
        MaterialOrderDTO result = materialOrderService.getMaterialOrderById(1L);

        // Verify the result
        assertEquals(materialOrderDTO, result);
    }

    @Test
    void testGetMaterialOrderById_InvalidId_ThrowsNotFoundException() {
        // Mocking the behavior of the repository when an ID is not found
        when(materialOrderRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // Call the service method with an invalid ID and expect an exception
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> materialOrderService.getMaterialOrderById(999L));
    }

    @Test
    void testValidate_ValidMaterialOrderDTO_NoExceptionThrown() {
        // Create a valid MaterialOrderDTO
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById methods to return true for valid IDs
        when(cartonRepository.existsById(1L)).thenReturn(true);

        // Call the validate method and expect no exception to be thrown
        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_MissingMaterialId_ThrowsValidationException() {
        // Create a MaterialOrderDTO with a null material ID
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_InvalidMaterialType_ThrowsValidationException() {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("INVALID_TYPE");
        materialOrderDTO.setOrderedQuantity(10);

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_InvalidCartonId_ThrowsValidationException() {
        // Create a MaterialOrderDTO with an invalid carton ID
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById method to return false for the carton ID
        when(cartonRepository.existsById(1L)).thenReturn(false);

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }


    @Test
    void testValidate_ValidPackageMaterialOrderDTO_NoExceptionThrown() {
        // Create a valid MaterialOrderDTO with material type "PACKAGE"
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("PACKAGE");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById method to return true for the package ID
        when(packageRepository.existsById(1L)).thenReturn(true);

        // Call the validate method and expect no exception to be thrown
        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_ValidPlateMaterialOrderDTO_NoExceptionThrown() {
        // Create a valid MaterialOrderDTO with material type "PLATE"
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("PLATE");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById method to return true for the plate ID
        when(plateRepository.existsById(1L)).thenReturn(true);

        // Call the validate method and expect no exception to be thrown
        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_InvalidPackageId_ThrowsValidationException() {
        // Create a MaterialOrderDTO with an invalid package ID
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("PACKAGE");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById method to return false for the package ID
        when(packageRepository.existsById(1L)).thenReturn(false);

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_InvalidPlateId_ThrowsValidationException() {
        // Create a MaterialOrderDTO with an invalid plate ID
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("PLATE");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the existsById method to return false for the plate ID
        when(plateRepository.existsById(1L)).thenReturn(false);

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }

    @Test
    void testValidate_NonPositiveOrderedQuantity_ThrowsValidationException() {
        // Create a MaterialOrderDTO with non-positive ordered quantity
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(0); // <= 0

        // Call the validate method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
    }


    @Test
    void testDeleteMaterialOrderDeleted() throws ChangeSetPersister.NotFoundException {
        // Create a MaterialOrder with the provided ID
        long orderIdToDelete = 1L;
        MaterialOrder materialOrderToDelete = new MaterialOrder();
        materialOrderToDelete.setId(orderIdToDelete);
        materialOrderToDelete.setDeleted(false);

        // Mock the behavior of the repository to return the MaterialOrder when findByIdAndDeletedFalse is called
        when(materialOrderRepository.findByIdAndDeletedFalse(orderIdToDelete)).thenReturn(java.util.Optional.of(materialOrderToDelete));

        // Call the deleteMaterialOrder method
        materialOrderService.deleteMaterialOrder(orderIdToDelete);

        // Verify that the materialOrderRepository.save method is called with the materialOrderToDelete and that its 'deleted' field is set to true
        verify(materialOrderRepository).save(materialOrderToDelete);

        // Verify that the materialOrderToDelete is set to deleted (true) after calling deleteMaterialOrder
        assertTrue(materialOrderToDelete.isDeleted());
    }

    @Test
    void testDeleteMaterialOrder_InvalidId_ThrowsNotFoundException() {
        // Create an invalid ID that doesn't exist in the repository
        long invalidOrderId = 999L;

        // Mock the behavior of the repository to return an empty Optional when findByIdAndDeletedFalse is called with the invalid ID
        when(materialOrderRepository.findByIdAndDeletedFalse(invalidOrderId)).thenReturn(java.util.Optional.empty());

        // Call the deleteMaterialOrder method with the invalid ID and expect a NotFoundException
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> materialOrderService.deleteMaterialOrder(invalidOrderId));

    }


    @Test
    void testCreateMaterialOrder_ThrowsValidationException() {
        // Create a valid MaterialOrderDTO
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the behavior of the cartonRepository.existsById method to return false for the invalid Carton ID
        when(cartonRepository.existsById(1L)).thenReturn(false);

        // Call the createMaterialOrder method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.createMaterialOrder(materialOrderDTO));

        // Verify that the cartonRepository.existsById method is called with the provided Carton ID
        verify(cartonRepository).existsById(1L);

        // Verify that the materialOrderRepository.save method is not called since the validation should fail before saving
        verify(materialOrderRepository, never()).save(any(MaterialOrder.class));
    }

    @Test
    void testCreateMaterialOrder_InvalidCartonId_ThrowsValidationException() {
        // Create a valid MaterialOrderDTO
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the behavior of the cartonRepository.existsById method to return false for the invalid Carton ID
        when(cartonRepository.existsById(1L)).thenReturn(false);

        // Call the createMaterialOrder method and expect a ValidationException to be thrown
        assertThrows(ValidationException.class, () -> materialOrderService.createMaterialOrder(materialOrderDTO));

        // Verify that the cartonRepository.existsById method is called with the provided Carton ID
        verify(cartonRepository).existsById(1L);

        // Verify that the materialOrderRepository.save method is not called since the validation should fail before saving
        verify(materialOrderRepository, never()).save(any(MaterialOrder.class));
    }


    @Test
    void testUpdateMaterialOrder_ValidMaterialOrderDTO_UpdatesAndReturnsUpdatedMaterialOrderDTO() throws ChangeSetPersister.NotFoundException {
        // Create a valid MaterialOrderDTO for updating
        Long existingMaterialOrderId = 1L;
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(2L); // Assuming we are updating the material ID to 2
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(15); // Assuming we are updating the ordered quantity to 15

        // Mock the behavior of the materialOrderRepository.findByIdAndDeletedFalse method to return the existing material order
        MaterialOrder existingMaterialOrder = new MaterialOrder();
        existingMaterialOrder.setId(existingMaterialOrderId);
        existingMaterialOrder.setMaterialId(1L); // Assuming the original material ID was 1
        existingMaterialOrder.setMaterialType(MaterialType.CARTON);
        existingMaterialOrder.setOrderedQuantity(10); // Assuming the original ordered quantity was 10
        when(materialOrderRepository.findByIdAndDeletedFalse(existingMaterialOrderId)).thenReturn(Optional.of(existingMaterialOrder));

        // Mock the behavior of the cartonRepository.existsById method to return true for the valid Carton ID
        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);

        // Mock the behavior of the modelMapper to return the updated MaterialOrder when mapping from DTO to entity
        MaterialOrder updatedMaterialOrder = new MaterialOrder();
        updatedMaterialOrder.setId(existingMaterialOrderId);
        updatedMaterialOrder.setMaterialId(materialOrderDTO.getMaterialId());
        updatedMaterialOrder.setMaterialType(MaterialType.CARTON);
        updatedMaterialOrder.setOrderedQuantity(materialOrderDTO.getOrderedQuantity());
        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(updatedMaterialOrder);

        // Mock the behavior of the modelMapper to return the updated MaterialOrderDTO when mapping from entity to DTO
        MaterialOrderDTO updatedMaterialOrderDTO = new MaterialOrderDTO();
        updatedMaterialOrderDTO.setMaterialId(materialOrderDTO.getMaterialId());
        updatedMaterialOrderDTO.setMaterialType(MaterialType.CARTON.name());
        updatedMaterialOrderDTO.setOrderedQuantity(materialOrderDTO.getOrderedQuantity());
        when(modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class)).thenReturn(updatedMaterialOrderDTO);

        // Call the updateMaterialOrder method
        MaterialOrderDTO result = materialOrderService.updateMaterialOrder(existingMaterialOrderId, materialOrderDTO);

        // Verify that the materialOrderRepository.findByIdAndDeletedFalse method is called with the provided ID
        verify(materialOrderRepository).findByIdAndDeletedFalse(existingMaterialOrderId);

        // Verify that the cartonRepository.existsById method is called with the updated material ID
        verify(cartonRepository).existsById(materialOrderDTO.getMaterialId());

        // Verify that the materialOrderRepository.save method is called with the updated entity
        verify(materialOrderRepository).save(updatedMaterialOrder);

        // Verify that the modelMapper.map method is called with the updated MaterialOrder
        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);

        // Verify that the modelMapper.map method is called with the updated MaterialOrderDTO
        verify(modelMapper).map(updatedMaterialOrder, MaterialOrderDTO.class);

        // Verify that the returned MaterialOrderDTO contains the updated material ID and ordered quantity
        assertEquals(materialOrderDTO.getMaterialId(), result.getMaterialId());
        assertEquals(materialOrderDTO.getOrderedQuantity(), result.getOrderedQuantity());
    }

    @Test
    void testCreateMaterialOrder_ValidMaterialOrderDTO_CreatesAndReturnsMaterialOrderDTO() {
        // Create a valid MaterialOrderDTO
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L); // Assuming we have a valid Carton ID
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the behavior of the cartonRepository.existsById method to return true for the valid Carton ID
        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);

        // Mock the behavior of the modelMapper to return a MaterialOrder when mapping from DTO to entity
        MaterialOrder materialOrderEntity = new MaterialOrder();
        materialOrderEntity.setId(1L);
        when(modelMapper.map(materialOrderDTO, MaterialOrder.class)).thenReturn(materialOrderEntity);

        // Mock the behavior of the materialOrderRepository to return the created MaterialOrder
        MaterialOrder createdMaterialOrder = new MaterialOrder();
        createdMaterialOrder.setId(1L);
        when(materialOrderRepository.save(any(MaterialOrder.class))).thenReturn(createdMaterialOrder);

        // Call the createMaterialOrder method
        MaterialOrderDTO result = materialOrderService.createMaterialOrder(materialOrderDTO);

        // Verify that the cartonRepository.existsById method is called with the Carton ID from the MaterialOrderDTO
        verify(cartonRepository).existsById(materialOrderDTO.getMaterialId());

        // Verify that the modelMapper.map method is called with the input DTO
        verify(modelMapper).map(materialOrderDTO, MaterialOrder.class);

        // Verify that the materialOrderRepository.save method is called with the created entity
        verify(materialOrderRepository).save(materialOrderEntity);

        // Verify that the returned MaterialOrderDTO contains the correct ID
        assertEquals(createdMaterialOrder.getId(), result.getId());
    }

    @Test
    void testValidate_ValidMaterialOrderDTO_DoesNotThrowException() {
        // Create a valid MaterialOrderDTO
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(10);

        // Mock the behavior of the cartonRepository.existsById method to return true for the valid Carton ID
        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);

        // Call the validate method
        assertDoesNotThrow(() -> materialOrderService.validate(materialOrderDTO));
    }


    @Test
    void testValidate_NegativeOrderedQuantity_ThrowsValidationException() {
        // Create an invalid MaterialOrderDTO with a negative ordered quantity
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("CARTON");
        materialOrderDTO.setOrderedQuantity(-5);

        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);

        // Call the validate method and expect ValidationException with specific message
        ValidationException exception = assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
        assertEquals("Ordered Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    void testValidate_NegativeOrderedQuantityy_ThrowsValidationException() {
        // Create an invalid MaterialOrderDTO with a negative ordered quantity
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(1L);
        materialOrderDTO.setMaterialType("INVALIt");
        materialOrderDTO.setOrderedQuantity(1);

        when(cartonRepository.existsById(materialOrderDTO.getMaterialId())).thenReturn(true);

        // Call the validate method and expect ValidationException with specific message
        ValidationException exception = assertThrows(ValidationException.class, () -> materialOrderService.validate(materialOrderDTO));
        assertEquals("Invalid Material Type", exception.getMessage());
    }

    @Test
    void testCalculateCartonInsufficientNumbers() {
        // Създайте обект от класа Package, който да бъде подаден на тествания метод
        Package packageEntity = new Package();
        packageEntity.setAvailableQuantity(10); // Поставете желаната стойност за тестване
        packageEntity.setPiecesCarton(2); // Поставете желаната стойност за тестване

        // Създайте обект от класа Carton и го свържете с packageEntity
        Carton carton = new Carton();
        carton.setAvailableQuantity(5); // Поставете желаната стойност за тестване
        packageEntity.setCartonId(carton);

        // Извикайте тествания метод
        int result = materialOrderService.calculateCartonInsufficientNumbers(packageEntity);

        // Проверете резултата
        assertEquals(10, result); // Очакваме резултатът да е 20
    }

    @Test
    void testCalculatePlateInsufficientNumbers() {
        // Създайте обект от класа Package, който да бъде подаден на тествания метод
        Package packageEntity = new Package();
        packageEntity.setPlateId(new Plate()); // Поставете желаната стойност за тестване
        packageEntity.getPlateId().setAvailableQuantity(5); // Поставете желаната стойност за тестване

        // Извикайте тествания метод
        int result = materialOrderService.calculatePlateInsufficientNumbers(packageEntity);

        // Проверете резултата
        assertEquals(5, result); // Очакваме резултатът да е 5
    }

    @Test
    void testCalculatePackageInsufficientNumbers() {
        // Създайте обект от класа Package, който да бъде подаден на тествания метод
        Package packageEntity = new Package();
        packageEntity.setAvailableQuantity(100); // Поставете желаната стойност за тестване

        // Извикайте тествания метод
        int result = materialOrderService.calculatePackageInsufficientNumbers(packageEntity);

        // Проверете резултата
        assertEquals(100, result); // Очакваме резултатът да е 100
    }

}