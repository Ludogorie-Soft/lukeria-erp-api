package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialOrderRepository extends JpaRepository<MaterialOrder, Long> {
    List<MaterialOrder> findByDeletedFalse();

    Optional<MaterialOrder> findByIdAndDeletedFalse(Long id);
//    @Query("SELECT mo FROM MaterialOrder mo JOIN mo.items moi WHERE moi.id = :itemId")
//    Optional<MaterialOrder> findByMaterialOrderItemId(@Param("itemId") Long itemId);
//    @Query("SELECT mo FROM MaterialOrder mo WHERE mo.id = (SELECT moi.orderId FROM MaterialOrderItem moi WHERE moi.id = :itemId)")
//    Optional<MaterialOrder> findByMaterialOrderItemId(@Param("itemId") Long itemId);
    @Query("SELECT mo FROM MaterialOrder mo JOIN mo.items moi WHERE moi.id = :itemId")
    Optional<MaterialOrder> findByMaterialOrderItemId(@Param("itemId") Long itemId);

}
