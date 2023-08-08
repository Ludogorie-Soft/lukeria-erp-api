package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.services.ClientQueryService;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class QueryController {
    ClientQueryService clientQueryService;

    @GetMapping("/order_product/{id}")
    public List<OrderProduct> getOrderProductsByOrderId(@PathVariable(name = "id") Long id){
        return clientQueryService.getOrderProductsByOrderId(id);
    }
}
