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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class MaterialOrderMapperTest {

    @InjectMocks
    private MaterialOrderMapper materialOrderMapper;

    @Mock
    private PackageRepository packageRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToEntity_WithValidMaterialOrderDTO() {
        MaterialOrderDTO dto = new MaterialOrderDTO();
        dto.setId(1L);
        dto.setOrderDate(LocalDate.now().atStartOfDay());
        dto.setStatus("New");
        dto.setArrivalDate(LocalDate.now());
        dto.setDeleted(false);

        MaterialOrderItemDTO itemDto = new MaterialOrderItemDTO();
        itemDto.setId(1L);
        itemDto.setMaterialType(MaterialType.PLATE);
        itemDto.setMaterialId(1L);
        itemDto.setOrderedQuantity(10);
        itemDto.setMaterialName("Plate Material");
        itemDto.setReceivedQuantity(10);

        dto.setItems(Collections.singletonList(itemDto));

        MaterialOrder result = materialOrderMapper.toEntity(dto);

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
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setId(1L);
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L);
        dto.setOrderedQuantity(5);
        dto.setMaterialName("Package Item");
        dto.setReceivedQuantity(10);

        MaterialOrder order = new MaterialOrder();
        order.setId(2L);

        when(packageRepository.findById(1L)).thenReturn(Optional.of(new Package()));

        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getMaterialType(), result.getMaterialType());
        assertEquals(dto.getMaterialId(), result.getMaterialId());
        assertEquals(dto.getOrderedQuantity(), result.getOrderedQuantity());
        assertEquals(dto.getMaterialName(), result.getMaterialName());
        assertEquals(order, result.getOrder());
    }

    @Test
    void testToItemEntity_WithMaterialTypePackage_ShouldGetPhoto() {
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L);
        dto.setOrderedQuantity(170);
        dto.setReceivedQuantity(10);

        MaterialOrder order = new MaterialOrder();

        Package pkg = new Package();
        pkg.setPhoto("photo_url");
        when(packageRepository.findById(1L)).thenReturn(Optional.of(pkg));

        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);

        assertNotNull(result);
        assertEquals("photo_url", result.getPhoto());
    }

    @Test
    void testToItemEntity_WithNullItems_ShouldNotThrowException() {
        MaterialOrderItemDTO dto = new MaterialOrderItemDTO();
        dto.setMaterialType(MaterialType.PACKAGE);
        dto.setMaterialId(1L);
        dto.setOrderedQuantity(5);
        dto.setMaterialName("Sample Material");
        dto.setReceivedQuantity(10);

        MaterialOrder order = new MaterialOrder();
        when(packageRepository.findById(anyLong())).thenReturn(Optional.empty());

        MaterialOrderItem result = materialOrderMapper.toItemEntity(dto, order);

        assertNotNull(result);
        assertNull(result.getPhoto());
        assertEquals(order, result.getOrder());
    }
}

