package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    // SELECT FOR UPDATE so concurrent writers wait until our tx commits.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithLock(@Param("id") Long id);

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
