package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.services.ClientQueryService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
@AllArgsConstructor
public class QueryController {
    ClientQueryService clientQueryService;
    ModelMapper modelMapper;

    @GetMapping("/order_product/{id}")
    public List<OrderProductDTO> getOrderProductsByOrderId(@PathVariable(name = "id") Long id) {
        List<OrderProduct> orderProductDTOS = clientQueryService.getOrderProductsByOrderId(id);
        return orderProductDTOS.stream().map(orderProduct -> modelMapper.map(orderProduct, OrderProductDTO.class)).toList();

    }
}
