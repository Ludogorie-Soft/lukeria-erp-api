package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrderItem;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.utils.MaterialOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class MaterialOrderMapperTest {

    @InjectMocks
    private MaterialOrderMapper materialOrderMapper;

    @Mock
    private PackageRepository packageRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToEntity_WithValidMaterialOrderDTO() {
        // Arrange
        MaterialOrderDTO dto = new MaterialOrderDTO();
        dto.setId(1L);
        dto.setOrderDate(LocalDate.now().atStartOfDay());
        dto.setStatus("New");
        dto.setArrivalDate(java.time.LocalDate.now());
        dto.setDeleted(false);

        MaterialOrderItemDTO itemDto = new MaterialOrderItemDTO();
        itemDto.setId(1L);
        itemDto.setMaterialType(MaterialType.PLATE); // or any valid MaterialType
        itemDto.setMaterialId(1L);
        itemDto.setOrderedQuantity(10);
        itemDto.setMaterialName("Plate Material");

        dto.setItems(Collections.singletonList(itemDto));

        // Act
        MaterialOrder result = materialOrderMapper.toEntity(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getStatus(), result.getStatus());
        assertEquals(dto.getArrivalDate(), result.getArrivalDate());
        assertFalse(result.isDeleted());
        assertEquals(1, result.getItems().size());
        MaterialOrderItem item = result.getItems().get(0);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getMaterialType(), item.getMaterialType());
        assertEquals(itemDto.getMaterialId(), item.getMaterialId());
        assertEquals(itemDto.getOrderedQuantity(), item.getOrderedQuantity());
        assertEquals(itemDto.getMaterialName(), item.getMaterialName());
    }

    @Test
    void testToItemEntity_WithValidMaterialOrderItemDTO() {
        // Arrange
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setId(1L);
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L);
        dto.setOrderedQuantity(5);
        dto.setMaterialName("Package Item");

        MaterialOrder order = new MaterialOrder();
        order.setId(2L); // Example order ID

        when(packageRepository.findById(1L)).thenReturn(java.util.Optional.of(new Package())); // Mock package retrieval

        // Act
        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getMaterialType(), result.getMaterialType());
        assertEquals(dto.getMaterialId(), result.getMaterialId());
        assertEquals(dto.getOrderedQuantity(), result.getOrderedQuantity());
        assertEquals(dto.getMaterialName(), result.getMaterialName());
        assertEquals(order, result.getOrder()); // Ensure the order reference is set
    }

    @Test
    void testToItemEntity_WithMaterialTypePackage_ShouldGetPhoto() {
        // Arrange
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L); // Assume this is a valid ID for the package
        dto.setOrderedQuantity(170);

        MaterialOrder order = new MaterialOrder();

        Package pkg = new Package();
        pkg.setPhoto("photo_url"); // Mock photo URL
        when(packageRepository.findById(1L)).thenReturn(java.util.Optional.of(pkg));

        // Act
        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);
        // Assert
        assertNotNull(result);
        assertEquals("photo_url", result.getPhoto()); // Check that the photo URL is set correctly
    }

    @Test
    void testToItemEntity_WithNullItems_ShouldNotThrowException() {
        // Arrange
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L);
        dto.setOrderedQuantity(5);
        dto.setMaterialName("Sample Material");

        MaterialOrder order = new MaterialOrder();
        when(packageRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // Act
        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);

        // Assert
        assertNotNull(result);
        assertNull(result.getPhoto()); // Ensure that no photo is set when no package exists
        assertEquals(order, result.getOrder()); // Ensure the order reference is set
    }
}

