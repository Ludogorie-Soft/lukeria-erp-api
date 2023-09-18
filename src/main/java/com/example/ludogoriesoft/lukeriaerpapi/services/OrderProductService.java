package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final PackageRepository packageRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<OrderProductDTO> getAllOrderProducts() {
        List<OrderProduct> orderProducts = orderProductRepository.findByDeletedFalse();
        return orderProducts.stream()
                .map(orderProduct -> modelMapper.map(orderProduct, OrderProductDTO.class))
                .toList();
    }

    public OrderProductDTO getOrderProductById(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProduct order = orderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderProductDTO.class);
    }

    void validateOrderProductDTO(OrderProductDTO orderDTO) {
        if (orderDTO.getOrderId() != null) {
            boolean orderExists = orderRepository.existsById(orderDTO.getOrderId());
            if (!orderExists) {
                throw new ValidationException("Order does not exist with ID: " + orderDTO.getOrderId());
            }
        } else {
            throw new ValidationException("Order ID cannot be null!");
        }
        if (orderDTO.getPackageId() != null) {
            boolean orderExists = packageRepository.existsById(orderDTO.getPackageId());
            if (!orderExists) {
                throw new ValidationException("Package does not exist with ID: " + orderDTO.getPackageId());
            }
        } else {
            throw new ValidationException("Package ID cannot be null!");
        }
    }

    public OrderProductDTO createOrderProduct(OrderProductDTO orderDTO) {
        validateOrderProductDTO(orderDTO);
        OrderProduct order = orderProductRepository.save(modelMapper.map(orderDTO, OrderProduct.class));
        return modelMapper.map(order, OrderProductDTO.class);
    }

    public OrderProductDTO updateOrderProduct(Long id, OrderProductDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderProductDTO(orderDTO);

        OrderProduct existingOrderProduct = orderProductRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        OrderProduct updatedOrderProduct = modelMapper.map(orderDTO, OrderProduct.class);
        updatedOrderProduct.setId(existingOrderProduct.getId());
        orderProductRepository.save(updatedOrderProduct);
        return modelMapper.map(updatedOrderProduct, OrderProductDTO.class);
    }

    public void deleteOrderProduct(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProduct order = orderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        orderProductRepository.save(order);
    }
    public void reductionQuantities(List<OrderProductDTO> orderProductList) throws ChangeSetPersister.NotFoundException {
        for (OrderProductDTO orderProduct : orderProductList) {
            Product product = productRepository.findByIdAndDeletedFalse(orderProduct.getPackageId())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            int availableQuantity = product.getAvailableQuantity();
            int quantityToReduce = orderProduct.getNumber();
            Product productForException = productRepository.findByIdAndDeletedFalse(orderProduct.getPackageId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
            if (availableQuantity < quantityToReduce) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient quantity available for product-" + productForException.toString());
            }
            product.setAvailableQuantity(availableQuantity - quantityToReduce);
            productRepository.save(product);
        }
    }
}
