package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MaterialOrderService {
    private final MaterialOrderRepository materialOrderRepository;
    private final ProductRepository productRepository;
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

    public MaterialOrderDTO updateMaterialOrder(Long id, MaterialOrderDTO materialOrderDTO) throws ChangeSetPersister.NotFoundException {
        validate(materialOrderDTO);
        Optional<Package> aPackage = Optional.empty();
        Optional<Plate> plate = Optional.empty();
        Optional<Carton> carton = Optional.empty();
        MaterialOrder existingMaterialOrder = materialOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (existingMaterialOrder.getReceivedQuantity() != null) {
            if (existingMaterialOrder.getMaterialType().equals(MaterialType.PACKAGE)) {
                aPackage = packageRepository.findByIdAndDeletedFalse(existingMaterialOrder.getMaterialId());
            } else if (existingMaterialOrder.getMaterialType().equals(MaterialType.PLATE)) {
                plate = plateRepository.findByIdAndDeletedFalse(existingMaterialOrder.getMaterialId());
            } else if (existingMaterialOrder.getMaterialType().equals(MaterialType.CARTON)) {
                carton = cartonRepository.findByIdAndDeletedFalse(existingMaterialOrder.getMaterialId());
            }
            if(aPackage.isPresent()){
                aPackage.get().setAvailableQuantity(aPackage.get().getAvailableQuantity() - existingMaterialOrder.getReceivedQuantity() + materialOrderDTO.getReceivedQuantity());
                packageRepository.save(aPackage.get());
            } else if (plate.isPresent()) {
                plate.get().setAvailableQuantity(plate.get().getAvailableQuantity() - existingMaterialOrder.getReceivedQuantity() + materialOrderDTO.getReceivedQuantity());
                plateRepository.save(plate.get());
            } else if (carton.isPresent()) {
                carton.get().setAvailableQuantity(carton.get().getAvailableQuantity() - existingMaterialOrder.getReceivedQuantity() + materialOrderDTO.getReceivedQuantity());
                cartonRepository.save(carton.get());
            }
        }
        MaterialOrder updatedMaterialOrder = modelMapper.map(materialOrderDTO, MaterialOrder.class);
        updatedMaterialOrder.setId(existingMaterialOrder.getId());
        if(existingMaterialOrder.getReceivedQuantity() == null) {
            increaseProductsQuantity(updatedMaterialOrder);
        }
        materialOrderRepository.save(updatedMaterialOrder);
        return modelMapper.map(updatedMaterialOrder, MaterialOrderDTO.class);
    }

    @Transactional
    public void increaseProductsQuantity(MaterialOrder updatedMaterialOrder) {
        if (updatedMaterialOrder.getReceivedQuantity() != null) {
            if (updatedMaterialOrder.getMaterialType().equals(MaterialType.CARTON)) {
                Carton carton = cartonRepository.findById(updatedMaterialOrder.getMaterialId()).orElseThrow(EntityNotFoundException::new);
                carton.setAvailableQuantity(carton.getAvailableQuantity() + updatedMaterialOrder.getReceivedQuantity());
                cartonRepository.save(carton);
            } else if (updatedMaterialOrder.getMaterialType().equals(MaterialType.PLATE)) {
                Plate plate = plateRepository.findById(updatedMaterialOrder.getMaterialId()).orElseThrow(EntityNotFoundException::new);
                plate.setAvailableQuantity(plate.getAvailableQuantity() + updatedMaterialOrder.getReceivedQuantity());
                plateRepository.save(plate);
            } else {
                Package aPackage = packageRepository.findById(updatedMaterialOrder.getMaterialId()).orElseThrow(EntityNotFoundException::new);
                aPackage.setAvailableQuantity(aPackage.getAvailableQuantity() + updatedMaterialOrder.getReceivedQuantity());
                packageRepository.save(aPackage);
            }
        }
    }

    public void deleteMaterialOrder(Long id) throws ChangeSetPersister.NotFoundException {
        MaterialOrder materialOrder = materialOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        materialOrder.setDeleted(true);
        materialOrderRepository.save(materialOrder);
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

    public List<MaterialOrderDTO> getAllOrderProductsByOrderId(Long orderId) {
        List<OrderProduct> filteredOrderProducts = orderProductRepository.findByDeletedFalse().stream()
                .filter(orderProduct -> orderProduct.getOrderId().getId().equals(orderId)).toList();
        return getProductsByPackageId(filteredOrderProducts);
    }

    public List<MaterialOrderDTO> getProductsByPackageId(List<OrderProduct> orderProducts) {
        List<MaterialOrderDTO> materialsForOrder = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            Package packageEntity = orderProduct.getPackageId();
            Product product = productRepository.findByPackageIdAndDeletedFalse(packageEntity)
                    .orElseThrow(() -> new RuntimeException("Продуктът не беше намерен"));
            if (product.getAvailableQuantity() < orderProduct.getNumber()) {
                if (calculatePlateInsufficientNumbers(packageEntity) < orderProduct.getNumber()) {
                    createMaterialOrder(MaterialType.PLATE, packageEntity.getPlateId().getId(), calculatePlateInsufficientNumbers(packageEntity) - orderProduct.getNumber(), materialsForOrder);
                }
                if (calculateCartonInsufficientNumbers(packageEntity) < orderProduct.getNumber()) {
                    createMaterialOrder(MaterialType.CARTON, packageEntity.getCartonId().getId(),
                            (calculateCartonInsufficientNumbers(packageEntity) / packageEntity.getPiecesCarton()) - (orderProduct.getNumber() / packageEntity.getPiecesCarton()), materialsForOrder);
                }
                if (calculatePackageInsufficientNumbers(packageEntity) < orderProduct.getNumber()) {
                    createMaterialOrder(MaterialType.PACKAGE, packageEntity.getId(), calculatePackageInsufficientNumbers(packageEntity) - orderProduct.getNumber(), materialsForOrder);
                }
            }
        }
        return materialsForOrder;
    }

    public int calculateCartonInsufficientNumbers(Package packageEntity) {
        int piecesCarton = Optional.of(packageEntity.getPiecesCarton())
                .orElseThrow(() -> new RuntimeException("Няма посочени бройки в кашон"));

        return packageEntity.getCartonId().getAvailableQuantity() * piecesCarton;
    }


    public int calculatePlateInsufficientNumbers(Package packageEntity) {
        return Objects.requireNonNull(packageEntity.getPlateId().getAvailableQuantity(), "Няма посочени бройки на тарелка");

    }

    public int calculatePackageInsufficientNumbers(Package packageEntity) {
        return packageEntity.getAvailableQuantity();
    }

    public void createMaterialOrder(MaterialType materialType, Long materialId, int orderedQuantity, List<MaterialOrderDTO> materialsForOrder) {

        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setMaterialId(materialId);
        materialOrderDTO.setMaterialType(materialType.toString());
        materialOrderDTO.setOrderedQuantity(-1 * orderedQuantity);
        materialsForOrder.add(materialOrderDTO);
    }

    public List<MaterialOrderDTO> allOrderedProducts() {
        List<OrderProduct> orderProducts = orderProductRepository.findAll();

        Map<Long, Integer> packageIdToTotalNumberMap = orderProducts.stream()
                .filter(orderProduct -> orderProduct.getPackageId() != null)
                .collect(Collectors.groupingBy(
                        orderProduct -> orderProduct.getPackageId().getId(),
                        Collectors.summingInt(OrderProduct::getNumber)
                ));

        return packageIdToTotalNumberMap.entrySet().stream()
                .map(entry -> {
                    MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
                    materialOrderDTO.setMaterialId(entry.getKey());
                    materialOrderDTO.setOrderedQuantity(entry.getValue());
                    return materialOrderDTO;
                })
                .toList();
    }


    public List<MaterialOrderDTO> allMissingMaterials(List<MaterialOrderDTO> allNeedsMaterialOrders) {
        List<MaterialOrderDTO> allMaterialsForAllOrders = new ArrayList<>();
        for (MaterialOrderDTO allNeedsMaterialOrder : allNeedsMaterialOrders) {
            Package packageEntity = findPackageByMaterialId(allNeedsMaterialOrder.getMaterialId());
            if (packageEntity != null) {
                Product product = getProductFromPackage(packageEntity);
                if (product.getAvailableQuantity() < allNeedsMaterialOrder.getOrderedQuantity()) {
                    createPlateInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
                    createCartonInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
                    createPackageInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
                }
            }
        }
        return allMaterialsForAllOrders;
    }

    public Package findPackageByMaterialId(long materialId) {
        Optional<Package> optionalPackage = packageRepository.findByIdAndDeletedFalse(materialId);
        return optionalPackage.orElse(null);
    }

    public Product getProductFromPackage(Package packageEntity) {
        return productRepository.findByPackageIdAndDeletedFalse(packageEntity)
                .orElseThrow(() -> new RuntimeException("Продуктът не беше намерен"));
    }

    public void createPlateInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
        if (calculatePlateInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
            int orderedQuantity = calculatePlateInsufficientNumbers(packageEntity) - allNeedsMaterialOrder.getOrderedQuantity();
            createMaterialOrder(MaterialType.PLATE, packageEntity.getPlateId().getId(), orderedQuantity, allMaterialsForAllOrders);
        }
    }

    public void createCartonInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
        if (calculateCartonInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
            int orderedQuantity = (calculateCartonInsufficientNumbers(packageEntity) / packageEntity.getPiecesCarton()) - (allNeedsMaterialOrder.getOrderedQuantity() / packageEntity.getPiecesCarton());
            createMaterialOrder(MaterialType.CARTON, packageEntity.getCartonId().getId(), orderedQuantity, allMaterialsForAllOrders);
        }
    }

    public void createPackageInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
        if (calculatePackageInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
            int orderedQuantity = calculatePackageInsufficientNumbers(packageEntity) - allNeedsMaterialOrder.getOrderedQuantity();
            createMaterialOrder(MaterialType.PACKAGE, packageEntity.getId(), orderedQuantity, allMaterialsForAllOrders);
        }
    }

}
