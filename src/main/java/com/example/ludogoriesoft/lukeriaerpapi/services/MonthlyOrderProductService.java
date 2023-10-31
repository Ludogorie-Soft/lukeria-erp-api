package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MonthlyOrderProductService {
    private final MonthlyOrderProductRepository monthlyOrderProductRepository;
    private final MonthlyOrderRepository monthlyOrderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    public List<MonthlyOrderProductDTO> getAllMonthlyOrderProducts() {
        List<MonthlyOrderProduct> monthlyOrders = monthlyOrderProductRepository.findByDeletedFalse();
        return monthlyOrders.stream().map(monthlyOrder -> modelMapper.map(monthlyOrder, MonthlyOrderProductDTO.class)).toList();
    }

    public MonthlyOrderProductDTO getMonthlyOrderProductById(Long id) throws ChangeSetPersister.NotFoundException {
        MonthlyOrderProduct monthlyOrder = monthlyOrderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(monthlyOrder, MonthlyOrderProductDTO.class);
    }

    void validateMonthlyOrderProduct(MonthlyOrderProductDTO monthlyOrderProduct) {
        if (monthlyOrderProduct.getMonthlyOrderId() != null) {
            boolean orderExists = monthlyOrderRepository.existsById(monthlyOrderProduct.getMonthlyOrderId());
            if (!orderExists) {
                throw new ValidationException("Monthly Order does not exist with ID: " + monthlyOrderProduct.getMonthlyOrderId());
            }
        } else {
            throw new ValidationException("Monthly Order cannot be null");
        }
        if (monthlyOrderProduct.getProductId() != null) {
            boolean orderExists = productRepository.existsById(monthlyOrderProduct.getProductId());
            if (!orderExists) {
                throw new ValidationException("Product does not exist with ID: " + monthlyOrderProduct.getProductId());
            }
        } else {
            throw new ValidationException("Product cannot be null");
        }
        if(monthlyOrderProduct.getOrderedQuantity()<=0){
            throw new ValidationException("Ordered Quantity cannot lower than 0!");
        }

    }

    public MonthlyOrderProductDTO createMonthlyOrderProduct(MonthlyOrderProductDTO monthlyOrderProduct) {
        validateMonthlyOrderProduct(monthlyOrderProduct);
        monthlyOrderProductRepository.save(modelMapper.map(monthlyOrderProduct, MonthlyOrderProduct.class));
        return monthlyOrderProduct;
    }

    public MonthlyOrderProductDTO updateMonthlyOrderProduct(Long id, MonthlyOrderProductDTO monthlyOrder) throws ChangeSetPersister.NotFoundException {
        validateMonthlyOrderProduct(monthlyOrder);
        MonthlyOrderProduct existingOrder = monthlyOrderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        MonthlyOrderProduct updatedOrder = modelMapper.map(monthlyOrder, MonthlyOrderProduct.class);
        updatedOrder.setId(existingOrder.getId());
        monthlyOrderProductRepository.save(updatedOrder);
        return modelMapper.map(updatedOrder, MonthlyOrderProductDTO.class);
    }

    public void deleteMonthlyOrderProduct(Long id) throws ChangeSetPersister.NotFoundException {
        MonthlyOrderProduct order = monthlyOrderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        monthlyOrderProductRepository.save(order);
    }
}
