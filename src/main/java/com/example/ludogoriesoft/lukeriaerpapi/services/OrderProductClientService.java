package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProductClient;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderProductClientService {
    private final OrderProductClientRepository orderProductClientRepository;
    private final OrderRepository orderRepository;
    private final PackageRepository packageRepository;
    private final ModelMapper modelMapper;

    public List<OrderProductClientDTO> getAllOrderProductClients() {
        List<OrderProductClient> orderProductClients = orderProductClientRepository.findByDeletedFalse();
        return orderProductClients.stream()
                .map(orderProductClient -> modelMapper.map(orderProductClient, OrderProductClientDTO.class))
                .toList();
    }

    public OrderProductClientDTO getOrderProductClientById(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProductClient order = orderProductClientRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderProductClientDTO.class);
    }

    void validateOrderProductClientDTO(OrderProductClientDTO orderDTO) {
        if (orderDTO.getOrderId() != null) {
            boolean orderExists = orderRepository.existsById(orderDTO.getOrderId().getId());
            if (!orderExists) {
                throw new ValidationException("Order does not exist with ID: " + orderDTO.getOrderId().getId());
            }
        } else {
            throw new ValidationException("Order ID cannot be null!");
        }
        if (orderDTO.getPackageId() != null) {
            boolean orderExists = packageRepository.existsById(orderDTO.getPackageId().getId());
            if (!orderExists) {
                throw new ValidationException("Package does not exist with ID: " + orderDTO.getPackageId().getId());
            }
        } else {
            throw new ValidationException("Package ID cannot be null!");
        }
    }

    public OrderProductClientDTO createOrderProductClient(OrderProductClientDTO orderDTO) {
        validateOrderProductClientDTO(orderDTO);
        OrderProductClient order = orderProductClientRepository.save(modelMapper.map(orderDTO, OrderProductClient.class));
        return modelMapper.map(order, OrderProductClientDTO.class);
    }

    public OrderProductClientDTO updateOrderProductClient(Long id, OrderProductClientDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderProductClientDTO(orderDTO);

        OrderProductClient existingOrderProductClient = orderProductClientRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        OrderProductClient updatedOrderProductClient = modelMapper.map(orderDTO, OrderProductClient.class);
        updatedOrderProductClient.setId(existingOrderProductClient.getId());
        orderProductClientRepository.save(updatedOrderProductClient);
        return modelMapper.map(updatedOrderProductClient, OrderProductClientDTO.class);
    }

    public void deleteOrderProductClient(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProductClient order = orderProductClientRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        orderProductClientRepository.save(order);
    }

}
