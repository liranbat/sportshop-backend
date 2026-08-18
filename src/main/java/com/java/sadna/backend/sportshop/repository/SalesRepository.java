package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import com.java.sadna.backend.sportshop.model.SalesProductQuantityRowResultDto;
import com.java.sadna.backend.sportshop.model.SalesProductRevenueRowResultDto;
import com.java.sadna.backend.sportshop.model.SalesStatusRowResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface SalesRepository extends Repository<OrderEntity, Long> {

    @Query("""
            SELECT new com.java.sadna.backend.sportshop.model.SalesStatusRowResultDto(
                       o.status,
                       COUNT(o),
                       SUM(o.totalPrice))
              FROM OrderEntity o
             WHERE o.createdAt >= :fromInclusive
               AND o.createdAt < :toExclusive
             GROUP BY o.status
            """)
    List<SalesStatusRowResultDto> aggregateByStatus(@Param("fromInclusive") OffsetDateTime fromInclusive,
                                                    @Param("toExclusive") OffsetDateTime toExclusive);

    // Null when the range has no non-cancelled items (aggregate over an empty set).
    @Query("""
            SELECT SUM(oi.quantity)
              FROM OrderItemEntity oi, OrderEntity o
             WHERE oi.orderId = o.id
               AND o.createdAt >= :fromInclusive
               AND o.createdAt < :toExclusive
               AND o.status NOT IN ('CANCELLED_BY_USER', 'CANCELLED_BY_ADMIN')
            """)
    Long sumItemsSold(@Param("fromInclusive") OffsetDateTime fromInclusive,
                      @Param("toExclusive") OffsetDateTime toExclusive);

    @Query("""
            SELECT new com.java.sadna.backend.sportshop.model.SalesProductQuantityRowResultDto(
                       oi.productId,
                       SUM(oi.quantity))
              FROM OrderItemEntity oi, OrderEntity o
             WHERE oi.orderId = o.id
               AND o.createdAt >= :fromInclusive
               AND o.createdAt < :toExclusive
               AND o.status NOT IN ('CANCELLED_BY_USER', 'CANCELLED_BY_ADMIN')
             GROUP BY oi.productId
             ORDER BY SUM(oi.quantity) DESC, oi.productId ASC
            """)
    List<SalesProductQuantityRowResultDto> findTopProductsByQuantity(
            @Param("fromInclusive") OffsetDateTime fromInclusive,
            @Param("toExclusive") OffsetDateTime toExclusive,
            Pageable pageable);

    @Query("""
            SELECT new com.java.sadna.backend.sportshop.model.SalesProductRevenueRowResultDto(
                       oi.productId,
                       SUM(oi.quantity * oi.pricePerUnit))
              FROM OrderItemEntity oi, OrderEntity o
             WHERE oi.orderId = o.id
               AND o.createdAt >= :fromInclusive
               AND o.createdAt < :toExclusive
               AND o.status NOT IN ('CANCELLED_BY_USER', 'CANCELLED_BY_ADMIN')
             GROUP BY oi.productId
             ORDER BY SUM(oi.quantity * oi.pricePerUnit) DESC, oi.productId ASC
            """)
    List<SalesProductRevenueRowResultDto> findTopProductsByRevenue(
            @Param("fromInclusive") OffsetDateTime fromInclusive,
            @Param("toExclusive") OffsetDateTime toExclusive,
            Pageable pageable);
}
