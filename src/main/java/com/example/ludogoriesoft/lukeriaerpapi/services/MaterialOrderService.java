package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MaterialOrderService {
    private final MaterialOrderRepository materialOrderRepository;
    private final CartonRepository cartonRepository;
    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final OrderProductRepository orderProductRepository;


    private final ModelMapper modelMapper;

    public List<MaterialOrderDTO> getAllMaterialOrders() {
        List<MaterialOrder> materialOrders = materialOrderRepository.findByDeletedFalse();
        return materialOrders.stream().map(materialOrder -> modelMapper.map(materialOrder, MaterialOrderDTO.class)).toList();
    }

    public MaterialOrderDTO getMaterialOrderById(Long id) throws ChangeSetPersister.NotFoundException {
        MaterialOrder materialOrder = materialOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(materialOrder, MaterialOrderDTO.class);
    }

    public MaterialOrderDTO createMaterialOrder(MaterialOrderDTO materialOrderDTO) {
        validate(materialOrderDTO);
        MaterialOrder createdMaterialOrder = materialOrderRepository.save(modelMapper.map(materialOrderDTO, MaterialOrder.class));
        materialOrderDTO.setId(createdMaterialOrder.getId());
        return materialOrderDTO;
    }

    public void validate(MaterialOrderDTO materialOrderDTO) {
        if (materialOrderDTO.getMaterialId() == null) {
            throw new ValidationException("Material ID cannot be null");
        }
        if (! materialOrderDTO.getMaterialType().equals("CARTON") && ! materialOrderDTO.getMaterialType().equals("PACKAGE") && ! materialOrderDTO.getMaterialType().equals("PLATE")) {
            throw new ValidationException("Invalid Material Type");
        }
        switch (materialOrderDTO.getMaterialType()) {
            case "CARTON" -> {
                if (!cartonRepository.existsById(materialOrderDTO.getMaterialId())) {
                    throw new ValidationException("Invalid Carton ID");
                }
            }
            case "PACKAGE" -> {
                if (!packageRepository.existsById(materialOrderDTO.getMaterialId())) {
                    throw new ValidationException("Invalid Package ID");
                }
            }
            case "PLATE" -> {
                if (!plateRepository.existsById(materialOrderDTO.getMaterialId())) {
                    throw new ValidationException("Invalid Plate ID");
                }
            }
        }
        if (materialOrderDTO.getOrderedQuantity() <= 0) {
            throw new ValidationException("Ordered Quantity must be greater than zero");
        }
    }

    public MaterialOrderDTO updateMaterialOrder(Long id, MaterialOrderDTO materialOrderDTO) throws ChangeSetPersister.NotFoundException {
        validate(materialOrderDTO);

        MaterialOrder existingMaterialOrder = materialOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        MaterialOrder updatedMaterialOrder = modelMapper.map(materialOrderDTO, MaterialOrder.class);
        updatedMaterialOrder.setId(existingMaterialOrder.getId());
        materialOrderRepository.save(updatedMaterialOrder);
        return modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class);
    }


    public void deleteMaterialOrder(Long id) throws ChangeSetPersister.NotFoundException {
        MaterialOrder materialOrder = materialOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        materialOrder.setDeleted(true);
        materialOrderRepository.save(materialOrder);
    }

    public void getAllOrderProductsByOrderId(Long orderId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAll();
        List<OrderProduct> filteredOrderProducts = orderProducts.stream()
                .filter(orderProduct -> orderProduct.getOrderId().getId().equals(orderId)).toList();
        getProductsByPackageId( filteredOrderProducts);
    }

    public void getProductsByPackageId(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            int cartonInsufficientNumbers = calculateCartonInsufficientNumbers(orderProduct.getPackageId());
            int plateInsufficientNumbers = calculatePlateInsufficientNumbers(orderProduct.getPackageId());
            int packageInsufficientNumbers = calculatePackageInsufficientNumbers(orderProduct.getPackageId());

            if (plateInsufficientNumbers <= orderProduct.getNumber()) {
                createMaterialOrder(MaterialType.PLATE, orderProduct.getPackageId().getPlateId().getId(), plateInsufficientNumbers - orderProduct.getNumber());
            }

            if (cartonInsufficientNumbers <= orderProduct.getNumber()) {
                createMaterialOrder(MaterialType.CARTON, orderProduct.getPackageId().getCartonId().getId(),  (cartonInsufficientNumbers / orderProduct.getPackageId().getPiecesCarton()) - (orderProduct.getNumber() / orderProduct.getPackageId().getPiecesCarton()));
            }

            if (packageInsufficientNumbers <= orderProduct.getNumber()) {
                createMaterialOrder(MaterialType.PACKAGE, orderProduct.getPackageId().getId(), packageInsufficientNumbers - orderProduct.getNumber());
            }
        }
    }

    public int calculateCartonInsufficientNumbers(Package packageEntity) {
        return packageEntity.getCartonId().getAvailableQuantity() * packageEntity.getPiecesCarton();
    }

    public int calculatePlateInsufficientNumbers(Package packageEntity) {
        return packageEntity.getPlateId().getAvailableQuantity();
    }

    public int calculatePackageInsufficientNumbers(Package packageEntity) {
        return packageEntity.getAvailableQuantity();
    }

    public void createMaterialOrder(MaterialType materialType, Long materialId, int orderedQuantity) {

        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(materialId);
        materialOrderDTO.setMaterialType(materialType.toString());
        materialOrderDTO.setOrderedQuantity(-1 * orderedQuantity);

        createMaterialOrder(materialOrderDTO);
    }



}
