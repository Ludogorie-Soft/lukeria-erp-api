package com.example.ludogoriesoft.lukeriaerpapi.utils;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrderItem;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaterialOrderMapper {
    private final PackageRepository packageRepository;

    public MaterialOrder toEntity(MaterialOrderDTO dto) {
        MaterialOrder order = new MaterialOrder();
        order.setId(dto.getId());
        order.setOrderDate(dto.getOrderDate());
        order.setStatus(dto.getStatus());
        order.setArrivalDate(dto.getArrivalDate());
        order.setDeleted(dto.isDeleted());

        if (dto.getItems() != null) {
            List<MaterialOrderItem> items = dto.getItems().stream()
                    .map(itemDto -> toItemEntity(itemDto, order)) // Set order reference
                    .collect(Collectors.toList());
            order.setItems(items);
        }

        return order;
    }

    public MaterialOrderItem toItemEntity(MaterialOrderItemDTO dto, MaterialOrder order) {
        MaterialOrderItem item = new MaterialOrderItem();
        item.setId(dto.getId());
        item.setMaterialType(dto.getMaterialType());
        item.setMaterialId(dto.getMaterialId());
        item.setOrderedQuantity(dto.getOrderedQuantity());
        item.setReceivedQuantity(dto.getReceivedQuantity());
        item.setMaterialName(dto.getMaterialName());
        if (item.getMaterialType() != null && item.getMaterialType().toString().equals("PACKAGE")) {
            // Fetch the package photo
            var packageOptional = packageRepository.findById(item.getMaterialId());
            if (packageOptional.isPresent()) {
                item.setPhoto(packageOptional.get().getPhoto());
            } else {
                // Either leave the photo as null or handle it accordingly
                item.setPhoto(null); // Explicitly setting it to null if not found (this line is optional)
            }
        }
        item.setOrder(order); // ✅ Set the parent order
        return item;
    }
}

