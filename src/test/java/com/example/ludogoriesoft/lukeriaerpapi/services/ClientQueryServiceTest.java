package com.example.ludogoriesoft.lukeriaerpapi.services;
import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceTest {

    @InjectMocks
    private ClientQueryService clientQueryService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<OrderProduct> typedQuery;

    @Test
    void testGetOrderProductsByOrderId() {
        Long orderId = 1L;

        OrderProduct orderProduct1 = new OrderProduct();
        OrderProduct orderProduct2 = new OrderProduct();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct1);
        orderProducts.add(orderProduct2);

        when(entityManager.createQuery(anyString(), eq(OrderProduct.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(orderProducts);

        List<OrderProduct> result = clientQueryService.getOrderProductsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
