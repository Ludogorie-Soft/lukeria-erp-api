package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderWithProductsDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.InvoiceOrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Order;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderProductService;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orderProduct")
@AllArgsConstructor
public class OrderProductController {
    private final OrderProductService orderProductService;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderProductDTO>> getAllOrderProducts(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(orderProductService.getAllOrderProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductDTO> getOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductService.getOrderProductById(id));
    }

    @PostMapping
    public ResponseEntity<OrderProductDTO> createOrderProduct(@Valid @RequestBody OrderProductDTO orderDTO, @RequestHeader("Authorization") String auth) {
        return new ResponseEntity<>(orderProductService.createOrderProduct(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProductDTO> updateOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody OrderProductDTO orderDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductService.updateOrderProduct(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderProductById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        orderProductService.deleteOrderProduct(id);
        return ResponseEntity.ok("Order with id: " + id + " has been deleted successfully!");
    }

    @GetMapping("/lessening")
    public ResponseEntity<Boolean> findInvoiceOrderProductsByInvoiceId(@RequestParam Long invoiceId, @RequestHeader("Authorization") String auth) {
        List<InvoiceOrderProduct> invoiceOrderProductsList = orderProductService.findInvoiceOrderProductsByInvoiceId(invoiceId);
        return ResponseEntity.ok(orderProductService.reduceProducts(invoiceOrderProductsList));
    }

    @GetMapping("/order-products-by-orders")
    public ResponseEntity<List<OrderWithProductsDTO>> getOrderProductDTOsByOrderDTOs(@RequestParam(name = "id") Long id, @RequestHeader("Authorization") String auth) {
        List<Order> orders = orderService.getAllOrdersForClient(id);
        List<OrderWithProductsDTO> orderWithProductsDTOs = orderProductService.getOrderProductsOfOrders(orders);
        return ResponseEntity.ok(orderWithProductsDTOs);
    }

    @GetMapping("/getOrderProducts")
    public ResponseEntity<List<OrderProductDTO>> orderProducts(@RequestParam("orderId")Long id,@RequestHeader("Authorization") String auth ) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderProductService.getOrderProducts(id));
    }
}
