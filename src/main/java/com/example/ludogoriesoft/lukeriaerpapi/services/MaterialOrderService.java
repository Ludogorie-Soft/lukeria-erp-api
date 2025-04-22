package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.enums.OrderStatus;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import com.example.ludogoriesoft.lukeriaerpapi.utils.MaterialOrderMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType.CARTON;
import static com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType.PLATE;

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
    private final MaterialOrderMapper materialOrderMapper;
    private final MaterialOrderItemRepository materialOrderItemRepository;

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

        MaterialOrder existingMaterialOrder = materialOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (materialOrderDTO.getItems().isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        // Update existing order fields (except items)
        existingMaterialOrder.setOrderDate(materialOrderDTO.getOrderDate());
//        existingMaterialOrder.setSupplier(materialOrderDTO.getSupplier());
        existingMaterialOrder.setStatus(materialOrderDTO.getStatus());

        // Handle items update
        existingMaterialOrder.getItems().clear();
        List<MaterialOrderItem> updatedItems = materialOrderDTO.getItems().stream()
                .map(dto -> {
                    MaterialOrderItem item = modelMapper.map(dto, MaterialOrderItem.class);
                    item.setOrder(existingMaterialOrder);//setMaterialOrder(existingMaterialOrder);
                    return item;
                })
                .collect(Collectors.toList());

        existingMaterialOrder.getItems().addAll(updatedItems);

        // Save the updated entity
        materialOrderRepository.save(existingMaterialOrder);

        return modelMapper.map(existingMaterialOrder, MaterialOrderDTO.class);
    }


//    @Transactional
//    public void increaseProductsQuantity(MaterialOrder updatedMaterialOrder) {
//        if (!updatedMaterialOrder.getItems().isEmpty()) { // Fix: Loop should run if items exist
//            for (MaterialOrderItem item : updatedMaterialOrder.getItems()) { // Fix: Use enhanced for-loop
//                switch (item.getMaterialType()) {
//                    case CARTON -> {
//                        Carton carton = cartonRepository.findById(item.getMaterialId())
//                                .orElseThrow(EntityNotFoundException::new);
//                        carton.setAvailableQuantity(carton.getAvailableQuantity() + item.getOrderedQuantity()); // Fix: Use item.getReceivedQuantity()
//                        cartonRepository.save(carton);
//                    }
//                    case PLATE -> {
//                        Plate plate = plateRepository.findById(item.getMaterialId())
//                                .orElseThrow(EntityNotFoundException::new);
//                        plate.setAvailableQuantity(plate.getAvailableQuantity() + item.getOrderedQuantity());
//                        plateRepository.save(plate);
//                    }
//                    default -> {
//                        Package aPackage = packageRepository.findById(item.getMaterialId())
//                                .orElseThrow(EntityNotFoundException::new);
//                        aPackage.setAvailableQuantity(aPackage.getAvailableQuantity() + item.getOrderedQuantity());
//                        packageRepository.save(aPackage);
//                    }
//                }
//            }
//        }
//    }


    public void deleteMaterialOrder(Long id) throws ChangeSetPersister.NotFoundException {
        MaterialOrder materialOrder = materialOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        materialOrder.setDeleted(true);
        materialOrderRepository.save(materialOrder);
    }

    public MaterialOrderDTO createMaterialOrder(MaterialOrderDTO materialOrderDTO) {
        // ✅ Use custom mapper
        MaterialOrder materialOrder = materialOrderMapper.toEntity(materialOrderDTO);

        // ✅ Save to DB
        MaterialOrder savedOrder = materialOrderRepository.save(materialOrder);

        // ✅ Convert back to DTO (optional, can use ModelMapper here)
        materialOrderDTO.setId(savedOrder.getId());
        return materialOrderDTO;
    }

    public void validate(MaterialOrderDTO materialOrderDTO) {
        if (materialOrderDTO.getItems().isEmpty()) {
            throw new ValidationException("Material ID cannot be null");
        }
        for (int i = 0; i < materialOrderDTO.getItems().size(); i++) {
            switch (materialOrderDTO.getItems().get(i).getMaterialType().toString()) {
                case "CARTON" -> {
                    if (!cartonRepository.existsById(materialOrderDTO.getItems().get(i).getMaterialId())) {
                        throw new ValidationException("Invalid Carton ID: " + materialOrderDTO.getItems().get(i).getMaterialId());
                    }
                }
                case "PACKAGE" -> {
                    if (!packageRepository.existsById(materialOrderDTO.getItems().get(i).getMaterialId())) {
                        throw new ValidationException("Invalid Package ID: " + materialOrderDTO.getItems().get(i).getMaterialId());
                    }
                }
                case "PLATE" -> {
                    if (!plateRepository.existsById(materialOrderDTO.getItems().get(i).getMaterialId())) {
                        throw new ValidationException("Invalid Plate ID : " + materialOrderDTO.getItems().get(i).getMaterialId());
                    }
                }
                default -> throw new ValidationException("Invalid Material Type");
            }
        }
    }

    public List<MaterialOrderItemDTO> getAllOrderProductsByOrderId(Long orderId) {
        List<OrderProduct> filteredOrderProducts = orderProductRepository.findByDeletedFalse().stream()
                .filter(orderProduct -> orderProduct.getOrderId().getId().equals(orderId)).toList();
        return getProductsByPackageId(filteredOrderProducts);
    }

    public List<MaterialOrderItemDTO> getProductsByPackageId(List<OrderProduct> orderProducts) {
        List<MaterialOrderItemDTO> materialsForOrder = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            Package packageEntity = orderProduct.getPackageId();
            Product product = productRepository.findByPackageIdAndDeletedFalse(packageEntity)
                    .orElseThrow(() -> new RuntimeException("Продуктът не беше намерен"));
            if (product.getAvailableQuantity() < orderProduct.getNumber()) {
                if (calculatePlateInsufficientNumbers(packageEntity) < orderProduct.getNumber()) {
                    createMaterialOrder(PLATE, packageEntity.getPlateId().getId(), calculatePlateInsufficientNumbers(packageEntity) - orderProduct.getNumber(), materialsForOrder);
                }
                if (calculateCartonInsufficientNumbers(packageEntity) < orderProduct.getNumber()) {
                    createMaterialOrder(CARTON, packageEntity.getCartonId().getId(),
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

    public void createMaterialOrder(MaterialType materialType, Long materialId, int orderedQuantity, List<MaterialOrderItemDTO> materialsForOrder) {
        MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
        materialOrderItemDTO.setMaterialType(materialType);
        materialOrderItemDTO.setMaterialId(materialId);
        materialOrderItemDTO.setOrderedQuantity(-1 * orderedQuantity);
        materialsForOrder.add(materialOrderItemDTO);
    }

    public List<MaterialOrderItemDTO> allOrderedProducts() {
        List<OrderProduct> orderProducts = orderProductRepository.findAll();

        Map<Long, Integer> packageIdToTotalNumberMap = orderProducts.stream()
                .filter(orderProduct -> orderProduct.getPackageId() != null && !orderProduct.getOrderId().isInvoiced())
                .collect(Collectors.groupingBy(
                        orderProduct -> orderProduct.getPackageId().getId(),
                        Collectors.summingInt(OrderProduct::getNumber)
                ));

        return packageIdToTotalNumberMap.entrySet().stream()
                .map(entry -> {
                    MaterialOrderItemDTO materialOrderItemDTO = new MaterialOrderItemDTO();
                    materialOrderItemDTO.setMaterialId(entry.getKey());
                    materialOrderItemDTO.setOrderedQuantity(entry.getValue());
                    return materialOrderItemDTO;
                })
                .toList();
    }


//    public List<MaterialOrderDTO> allMissingMaterials(List<MaterialOrderDTO> allNeedsMaterialOrders) {
//        List<MaterialOrderDTO> allMaterialsForAllOrders = new ArrayList<>();
//        for (MaterialOrderDTO allNeedsMaterialOrder : allNeedsMaterialOrders) {
//            Package packageEntity = findPackageByMaterialId(allNeedsMaterialOrder.getMaterialId());
//            if (packageEntity != null) {
//                Product product = getProductFromPackage(packageEntity);
//                if (product.getAvailableQuantity() < allNeedsMaterialOrder.getOrderedQuantity()) {
//                    createPlateInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
//                    createCartonInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
//                    createPackageInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);
//                }
//            }
//        }
//        return allMaterialsForAllOrders;
//    }

    public Package findPackageByMaterialId(long materialId) {
        Optional<Package> optionalPackage = packageRepository.findByIdAndDeletedFalse(materialId);
        return optionalPackage.orElse(null);
    }

    public Product getProductFromPackage(Package packageEntity) {
        return productRepository.findByPackageIdAndDeletedFalse(packageEntity)
                .orElseThrow(() -> new RuntimeException("Продуктът не беше намерен"));
    }

    //    public void createPlateInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
//        if (calculatePlateInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
//            int orderedQuantity = calculatePlateInsufficientNumbers(packageEntity) - allNeedsMaterialOrder.getOrderedQuantity();
//            createMaterialOrder(PLATE, packageEntity.getPlateId().getId(), orderedQuantity, allMaterialsForAllOrders);
//        }
//    }
//
//    public void createCartonInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
//        if (calculateCartonInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
//            int orderedQuantity = (calculateCartonInsufficientNumbers(packageEntity) / packageEntity.getPiecesCarton()) - (allNeedsMaterialOrder.getOrderedQuantity() / packageEntity.getPiecesCarton());
//            createMaterialOrder(CARTON, packageEntity.getCartonId().getId(), orderedQuantity, allMaterialsForAllOrders);
//        }
//    }
//
//    public void createPackageInsufficientMaterialOrder(MaterialOrderDTO allNeedsMaterialOrder, Package packageEntity, List<MaterialOrderDTO> allMaterialsForAllOrders) {
//        if (calculatePackageInsufficientNumbers(packageEntity) < allNeedsMaterialOrder.getOrderedQuantity()) {
//            int orderedQuantity = calculatePackageInsufficientNumbers(packageEntity) - allNeedsMaterialOrder.getOrderedQuantity();
//            createMaterialOrder(MaterialType.PACKAGE, packageEntity.getId(), orderedQuantity, allMaterialsForAllOrders);
//        }
//    }
    public List<MaterialOrderItemDTO> getAllMaterialOrderItems() {
        List<MaterialOrderItem> materialOrderItems = materialOrderItemRepository.findAll();
        return materialOrderItems.stream().map(materialOrderItem -> modelMapper.map(materialOrderItem, MaterialOrderItemDTO.class)).toList();
    }

    public MaterialOrderItem updateMaterialOrderItem(MaterialOrderItemDTO materialOrderItemDTO) {
//        Optional<MaterialOrder> materialOrder = materialOrderRepository.findByMaterialOrderItemId(materialOrderItemDTO.getId());
        MaterialOrder materialOrder = materialOrderRepository.findByMaterialOrderItemId(materialOrderItemDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Material order cannot be found for the specified material order item"));

        MaterialOrderItem materialOrderItem = materialOrderMapper.toItemEntity(materialOrderItemDTO, materialOrder);
        materialOrderItemRepository.save(materialOrderItem);
        if (materialOrderItem.getMaterialType().toString().equals("CARTON")) {
            Optional<Carton> carton = cartonRepository.findById(materialOrderItem.getMaterialId());
            if (carton.isPresent()) {
                carton.get().setAvailableQuantity(carton.get().getAvailableQuantity() + materialOrderItem.getReceivedQuantity());
                cartonRepository.save(carton.get());
            } else {
                throw new NoSuchElementException("carton cannot be found");
            }
        } else if (materialOrderItem.getMaterialType().toString().equals("PACKAGE")) {
            Optional<Package> specificPackage = packageRepository.findById(materialOrderItem.getMaterialId());
            if (specificPackage.isPresent()) {
                specificPackage.get().setAvailableQuantity(specificPackage.get().getAvailableQuantity() + materialOrderItem.getReceivedQuantity());
                packageRepository.save(specificPackage.get());
            } else {
                throw new NoSuchElementException("package cannot be found");
            }
        } else if (materialOrderItem.getMaterialType().toString().equals("PLATE")) {
            Optional<Plate> plate = plateRepository.findById(materialOrderItem.getMaterialId());
            if (plate.isPresent()) {
                plate.get().setAvailableQuantity(plate.get().getAvailableQuantity() + materialOrderItem.getReceivedQuantity());
                plateRepository.save(plate.get());
            } else {
                throw new NoSuchElementException("plate cannot be found");
            }
        }
        return materialOrderItem;
    }

    public MaterialOrderDTO updateWholeMaterialOrder(Long id, MaterialOrderDTO materialOrderDTO) throws ChangeSetPersister.NotFoundException {
        MaterialOrder materialOrder = modelMapper.map(materialOrderDTO, MaterialOrder.class);
        List<MaterialOrderItem> items = materialOrder.getItems();

        for (MaterialOrderItem item : items){
            item.setOrder(materialOrder);
            if (item.getMaterialType().toString().equalsIgnoreCase("CARTON")) {
                Optional<Carton> carton = cartonRepository.findById(item.getMaterialId());
                if (carton.isPresent()) {
                    carton.get().setAvailableQuantity(carton.get().getAvailableQuantity() + item.getReceivedQuantity());
                    cartonRepository.save(carton.get());
                } else {
                    throw new NoSuchElementException("carton cannot be found");
                }
            } else if (item.getMaterialType().toString().equalsIgnoreCase("PACKAGE")) {
                Optional<Package> specificPackage = packageRepository.findById(item.getMaterialId());
                if (specificPackage.isPresent()) {
                    specificPackage.get().setAvailableQuantity(specificPackage.get().getAvailableQuantity() + item.getReceivedQuantity());
                    packageRepository.save(specificPackage.get());
                } else {
                    throw new NoSuchElementException("package cannot be found");
                }
            } else if (item.getMaterialType().toString().equals("PLATE")) {
                Optional<Plate> plate = plateRepository.findById(item.getMaterialId());
                if (plate.isPresent()) {
                    plate.get().setAvailableQuantity(plate.get().getAvailableQuantity() + item.getReceivedQuantity());
                    plateRepository.save(plate.get());
                } else {
                    throw new NoSuchElementException("plate cannot be found");
                }
            }
        }

        materialOrder.setStatus("COMPLETED");
        materialOrder.setId(id);
        return modelMapper.map(materialOrderRepository.save(materialOrder), MaterialOrderDTO.class);
    }
}
