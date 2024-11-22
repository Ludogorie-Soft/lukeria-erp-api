package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ShoppingCartDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CartItem;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.models.ShoppingCart;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartItemRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientUserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ShoppingCartRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShoppingCartService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final ClientUserRepository clientUserRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final CustomerCustomPriceRepository customerCustomPriceRepository;

    public void addToCart(Long productId, int quantity) throws ChangeSetPersister.NotFoundException {

        UserDTO authenticateUserDTO = userService.findAuthenticatedUser();
        ClientUser clientUser = clientUserRepository.findByUserIdAndDeletedFalse(authenticateUserDTO.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Client client = clientUser.getClient();
        ShoppingCart shoppingCart = shoppingCartRepository.findByClientId(client).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Product product = productRepository.findById(productId).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<CartItem> cartItemList = shoppingCart.getItems();
        boolean ifProductIsInCart = false;

        for (CartItem cartItem : cartItemList) {
            if (cartItem.getProductId().equals(product)) {
                if (cartItem.getQuantity() + quantity > product.getAvailableQuantity()) {
                    throw new IllegalArgumentException("There is no that much quantity");
                }
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                ifProductIsInCart = true;
                break;
            }
        }
        if (ifProductIsInCart == false) {
            if (quantity > product.getAvailableQuantity()) {
                throw new IllegalArgumentException("There is no that much quantity");
            }
            CartItem cartItem = new CartItem();
            cartItem.setProductId(product);
            if (cartItem.getQuantity() < 0) {
                throw new ValidationException("Quantity must be more than 0");
            }
            cartItem.setQuantity(quantity);

            Optional<CustomerCustomPrice> optionalCustomPrice = customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(client, product);
            if (optionalCustomPrice.isPresent()) {
                cartItem.setPrice(optionalCustomPrice.get().getPrice());
            } else {
                cartItem.setPrice(product.getPrice());
            }
            cartItem.setShoppingCartId(shoppingCart);
            cartItemRepository.save(cartItem);
            shoppingCart.getItems().add(cartItem);
        }
        shoppingCartRepository.save(shoppingCart);
    }

    public List<CartItemDTO> showCart() throws ChangeSetPersister.NotFoundException {

        UserDTO authenticateUserDTO = userService.findAuthenticatedUser();
        ClientUser clientUser = clientUserRepository.findByUserIdAndDeletedFalse(authenticateUserDTO.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Client client = clientUser.getClient();
        ShoppingCart shoppingCart = shoppingCartRepository.findByClientId(client).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<CartItemDTO> cartItemDTOs = shoppingCart.getItems()
                .stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class)) // Map each CartItem to CartItemDTO
                .toList();

        return cartItemDTOs;
    }

    public void removeCartItem(Long cartItemId) throws ChangeSetPersister.NotFoundException {

        UserDTO authenticateUserDTO = userService.findAuthenticatedUser();
        ClientUser clientUser = clientUserRepository.findByUserIdAndDeletedFalse(authenticateUserDTO.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Client client = clientUser.getClient();
        ShoppingCart shoppingCart = shoppingCartRepository.findByClientId(client).orElseThrow(ChangeSetPersister.NotFoundException::new);

        CartItem cartItem = cartItemRepository.findByIdAndDeletedFalse(cartItemId).orElseThrow(ChangeSetPersister.NotFoundException::new);

        shoppingCart.getItems().remove(cartItem);
        cartItem.setShoppingCartId(null);
        cartItem.setDeleted(true);
        cartItemRepository.save(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    public void updateQuantityOfItem(Long cartItemId, int quantity) throws ChangeSetPersister.NotFoundException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be more than 0");
        }
        CartItem cartItem = cartItemRepository.findByIdAndDeletedFalse(cartItemId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (quantity > cartItem.getProductId().getAvailableQuantity()) {
            throw new IllegalArgumentException("There is no that much quantity");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
