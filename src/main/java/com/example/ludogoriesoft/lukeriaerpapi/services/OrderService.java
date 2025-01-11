package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CartItem;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.models.ShoppingCart;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientUserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.OrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ShoppingCartRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final ClientUserRepository clientUserRepository;
    private final UserService userService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerCustomPriceRepository customerCustomPriceRepository;
    private final ProductRepository productRepository;
    private final OrderProductService orderProductService;

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
        if (client.isPresent()) {
            return orderRepository.findAllByClientId(client.get());
        }
        throw new NoSuchElementException();
    }

    public void createOrderFromShoppingCart() throws ChangeSetPersister.NotFoundException {

        UserDTO authenticateUserDTO = userService.findAuthenticatedUser();
        ClientUser clientUser = clientUserRepository.findByUserIdAndDeletedFalse(authenticateUserDTO.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Client client = clientUser.getClient();
        ShoppingCart shoppingCart = shoppingCartRepository.findByClientId(client).orElseThrow(ChangeSetPersister.NotFoundException::new);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setClientId(client.getId());
        OrderDTO order = createOrder(orderDTO);

        if (shoppingCart.getItems().isEmpty()) {
            throw new ValidationException("Cart item is empty!");
        }

        for (CartItem cartItem : shoppingCart.getItems()) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setOrderId(order.getId());
            orderProductDTO.setNumber(cartItem.getQuantity());
            orderProductDTO.setPackageId(cartItem.getProductId().getPackageId().getId());

            //reduce available quantity
            cartItem.getProductId().setAvailableQuantity(cartItem.getProductId().getAvailableQuantity() - cartItem.getQuantity());
            productRepository.save(cartItem.getProductId());

            Optional<CustomerCustomPrice> optionalCustomPrice = customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, cartItem.getProductId());
            if (optionalCustomPrice.isPresent()) {
                orderProductDTO.setSellingPrice(optionalCustomPrice.get().getPrice());
            } else {
                orderProductDTO.setSellingPrice(cartItem.getProductId().getPrice());
            }

            orderProductService.createOrderProduct(orderProductDTO);
        }
        shoppingCart.getItems().clear();
        shoppingCartRepository.save(shoppingCart);

    }


}
