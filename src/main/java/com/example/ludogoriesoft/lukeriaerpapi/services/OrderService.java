package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findByDeletedFalse();
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    public OrderDTO getOrderById(Long id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderDTO.class);
    }

    void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO.getClientId() != null) {
            boolean orderExists = clientRepository.existsById(orderDTO.getClientId());
            if (!orderExists) {
                throw new ValidationException("Client does not exist with ID: " + orderDTO.getClientId());
            }
        } else {
            throw new ValidationException("Client ID cannot be null");
        }
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        validateOrderDTO(orderDTO);
        orderDTO.setOrderDate(orderDTO.getOrderDate());
        Order order = orderRepository.save(modelMapper.map(orderDTO, Order.class));
        return modelMapper.map(order, OrderDTO.class);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderDTO(orderDTO);
        Order existingOrder = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Order updatedOrder = modelMapper.map(orderDTO, Order.class);
        updatedOrder.setId(existingOrder.getId());
        updatedOrder.setOrderDate(orderDTO.getOrderDate());
        orderRepository.save(updatedOrder);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    public void deleteOrder(Long id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    public OrderDTO findFirstByOrderByIdDesc() {
        return modelMapper.map(orderRepository.findFirstByDeletedFalseOrderByIdDesc(), OrderDTO.class);
    }

    public List<Order> getAllOrdersForClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if(client.isPresent()) {
            return orderRepository.findAllByClientId(client.get());
        }
        throw new NoSuchElementException();
    }

}
