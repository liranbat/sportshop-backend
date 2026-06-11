package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
