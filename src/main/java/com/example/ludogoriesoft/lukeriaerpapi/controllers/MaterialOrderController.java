package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrderItem;
import com.example.ludogoriesoft.lukeriaerpapi.services.MaterialOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/material-order")
@AllArgsConstructor
public class MaterialOrderController {
    private final MaterialOrderService materialOrderService;

    @GetMapping
    public ResponseEntity<List<MaterialOrderDTO>> getAllMaterialOrders(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(materialOrderService.getAllMaterialOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialOrderDTO> getMaterialOrderById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(materialOrderService.getMaterialOrderById(id));
    }

    @PostMapping
    public ResponseEntity<MaterialOrderDTO> createMaterialOrder(@Valid @RequestBody MaterialOrderDTO materialOrderDTO, @RequestHeader("Authorization") String auth) {
        return new ResponseEntity<>(materialOrderService.createMaterialOrder(materialOrderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialOrderDTO> updateMaterialOrder(@PathVariable("id") Long id, @Valid @RequestBody MaterialOrderDTO materialOrderDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(materialOrderService.updateMaterialOrder(id, materialOrderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterialOrderById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        materialOrderService.deleteMaterialOrder(id);
        return ResponseEntity.ok("Material Order with id: " + id + " has been deleted successfully!");
    }
    @PostMapping("/submit")
    public ResponseEntity<?> submitMaterialOrder(@Valid @RequestBody MaterialOrderDTO materialOrderDTO,
                                                 @RequestHeader("Authorization") String auth) {
        try {
            MaterialOrderDTO createdOrder = materialOrderService.createMaterialOrder(materialOrderDTO);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Грешка при създаването на поръчка: " + e.getMessage());
        }
    }
    @GetMapping("/items")
    public ResponseEntity<List<MaterialOrderItemDTO>> getAllMaterialOrdersItems(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(materialOrderService.getAllMaterialOrderItems());
    }
    @PutMapping("/item/update")
    public ResponseEntity<?> updateMaterialOrdersItem(@Valid @RequestBody MaterialOrderItemDTO materialOrderItemDTO, @RequestHeader("Authorization") String auth) {
        try {
            MaterialOrderItem updatedMaterialOrderItem = materialOrderService.updateMaterialOrderItem(materialOrderItemDTO);
            return ResponseEntity.ok(materialOrderItemDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Грешка при създаването на поръчка: " + e.getMessage());
        }
    }

//    @GetMapping("products/{id}")
//    public List<MaterialOrderDTO> getAllProductsByOrderId(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) {
//        return materialOrderService.getAllOrderProductsByOrderId(id);
//    }
//
//    @GetMapping("/all-ordered-products")
//    public List<MaterialOrderDTO> allOrderedProducts(@RequestHeader("Authorization") String auth) {
//        return materialOrderService.allOrderedProducts();
//
//    }
//
//    @GetMapping("/all-missing-materials")
//    public List<MaterialOrderDTO> allAvailableProducts(@RequestHeader("Authorization") String auth) {
//        List<MaterialOrderDTO> allOrderedProducts = materialOrderService.allOrderedProducts();
//        return materialOrderService.allMissingMaterials(allOrderedProducts);
//    }

}
