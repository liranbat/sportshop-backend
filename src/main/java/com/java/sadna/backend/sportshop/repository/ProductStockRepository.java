package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStockRepository extends JpaRepository<ProductStockEntity, ProductStockId> {

    List<ProductStockEntity> findByProductId(Long productId);
}
