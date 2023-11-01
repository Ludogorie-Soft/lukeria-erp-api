package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.MonthlyOrder;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MonthlyOrderRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MonthlyOrderService {
    private final MonthlyOrderRepository monthlyOrderRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;


    public List<MonthlyOrderDTO> getAllMonthlyOrders() {
        List<MonthlyOrder> monthlyOrders = monthlyOrderRepository.findByDeletedFalse();
        return monthlyOrders.stream().map(monthlyOrder -> modelMapper.map(monthlyOrder, MonthlyOrderDTO.class)).toList();
    }

    public MonthlyOrderDTO getMonthlyOrderById(Long id) throws ChangeSetPersister.NotFoundException {
        MonthlyOrder monthlyOrder = monthlyOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(monthlyOrder, MonthlyOrderDTO.class);
    }

    void validateMonthlyOrder(MonthlyOrderDTO monthlyOrder) {
        if (monthlyOrder.getClientId() != null) {
            boolean orderExists = clientRepository.existsById(monthlyOrder.getClientId());
            if (!orderExists) {
                throw new ValidationException("Client does not exist with ID: " + monthlyOrder.getClientId());
            }
        } else {
            throw new ValidationException("Client ID cannot be null");
        }
    }

    public MonthlyOrderDTO createMonthlyOrder(MonthlyOrderDTO monthlyOrder) {
        validateMonthlyOrder(monthlyOrder);
        monthlyOrder.setStartDate(monthlyOrder.getStartDate());
        monthlyOrder.setEndDate(monthlyOrder.getEndDate());
        MonthlyOrder monthlyOrder1 = monthlyOrderRepository.save(modelMapper.map(monthlyOrder, MonthlyOrder.class));
        return modelMapper.map(monthlyOrder1, MonthlyOrderDTO.class);
    }

    public MonthlyOrderDTO updateMonthlyOrder(Long id, MonthlyOrderDTO monthlyOrder) throws ChangeSetPersister.NotFoundException {
        validateMonthlyOrder(monthlyOrder);
        MonthlyOrder existingOrder = monthlyOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        MonthlyOrder updatedOrder = modelMapper.map(monthlyOrder, MonthlyOrder.class);
        updatedOrder.setId(existingOrder.getId());
        updatedOrder.setStartDate(monthlyOrder.getStartDate());
        updatedOrder.setEndDate(monthlyOrder.getEndDate());
        monthlyOrderRepository.save(updatedOrder);
        return modelMapper.map(updatedOrder, MonthlyOrderDTO.class);
    }

    public void deleteMonthlyOrder(Long id) throws ChangeSetPersister.NotFoundException {
        MonthlyOrder order = monthlyOrderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        monthlyOrderRepository.save(order);
    }
    public MonthlyOrderDTO findFirstByOrderByIdDesc(){
        return modelMapper.map(monthlyOrderRepository.findFirstByDeletedFalseOrderByIdDesc(), MonthlyOrderDTO.class);
    }
}