package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.OrderProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientQueryService {

    @PersistenceContext
    public EntityManager entityManager;

    public List<OrderProduct> getOrderProductsByOrderId(Long orderId) {
        String jpqlQuery = "SELECT op FROM OrderProduct op " +
                "WHERE op.orderId.id = :orderId AND op.deleted = false";

        TypedQuery<OrderProduct> query = entityManager.createQuery(jpqlQuery, OrderProduct.class);
        query.setParameter("orderId", orderId);

        return query.getResultList();
    }
}
