package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ShoppingCartDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CartItem;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.models.ShoppingCart;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartItemRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientUserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ShoppingCartRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.ShoppingCartService;
import com.example.ludogoriesoft.lukeriaerpapi.services.UserService;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shoppingCart")
@AllArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping("/addToCart")
    public void addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        shoppingCartService.addToCart(productId, quantity);
    }

    @GetMapping("/showCart")
    public ResponseEntity<List<CartItemDTO>> showCart(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(shoppingCartService.showCart());
    }

    @PostMapping("/removeCartItem")
    public void removeCartItem(@RequestParam("cartItemId") Long cartItemId, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        shoppingCartService.removeCartItem(cartItemId);
    }

    @PutMapping("/updateQuantity")
    private void updateQuantity(@RequestParam("cartItemId") Long cartItemId,int quantity, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        shoppingCartService.updateQuantityOfItem(cartItemId,quantity);
    }
}
