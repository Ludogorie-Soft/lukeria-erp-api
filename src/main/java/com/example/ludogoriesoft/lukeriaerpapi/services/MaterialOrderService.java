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
        String materialTypeStr = materialOrderDTO.getMaterialType();
        if (!materialTypeStr.equals("CARTON") && !materialTypeStr.equals("PACKAGE") && !materialTypeStr.equals("PLATE")) {
            throw new ValidationException("Invalid Material Type");
        }
        switch (materialTypeStr) {
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
            default -> throw new ValidationException("Invalid Material Type");
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

    public void getProductsByPackageId( List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            Package packageEntity = orderProduct.getPackageId();
            int cartonInsufficientNumbers = calculateCartonInsufficientNumbers(packageEntity);
            int plateInsufficientNumbers = calculatePlateInsufficientNumbers(packageEntity);
            int packageInsufficientNumbers = calculatePackageInsufficientNumbers(packageEntity);

            System.err.println("брой поръчани-" + orderProduct.getNumber());
            System.err.println("тарелки -" + plateInsufficientNumbers);
            System.err.println("кашони-" + cartonInsufficientNumbers / packageEntity.getPiecesCarton());
            System.err.println("брой в кашон кутиия-" + packageEntity.getPiecesCarton());
            System.err.println("кутиия-" + packageInsufficientNumbers);
            System.err.println("                             ");

            if (plateInsufficientNumbers <= orderProduct.getNumber()) {
                int orderedQuantity = plateInsufficientNumbers - orderProduct.getNumber();
                createMaterialOrder(MaterialType.PLATE, packageEntity.getPlateId().getId(), orderedQuantity);
            }

            if (cartonInsufficientNumbers <= orderProduct.getNumber()) {
                int orderedQuantity = (cartonInsufficientNumbers / packageEntity.getPiecesCarton()) - (orderProduct.getNumber() / packageEntity.getPiecesCarton());
                createMaterialOrder(MaterialType.CARTON, packageEntity.getCartonId().getId(), orderedQuantity);
            }

            if (packageInsufficientNumbers <= orderProduct.getNumber()) {
                int orderedQuantity = packageInsufficientNumbers - orderProduct.getNumber();
                createMaterialOrder(MaterialType.PACKAGE, packageEntity.getId(), orderedQuantity);
            }

            System.err.println("                             ");
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
