package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE ProductEntity p
               SET p.categoryId = :toCategoryId,
                   p.version    = p.version + 1,
                   p.updatedAt  = :now,
                   p.updatedBy  = :actorId
             WHERE p.categoryId = :fromCategoryId
            """)
    int bulkReassignByCategoryId(@Param("fromCategoryId") Long fromCategoryId,
                                 @Param("toCategoryId") Long toCategoryId,
                                 @Param("actorId") Long actorId,
                                 @Param("now") OffsetDateTime now);
}
