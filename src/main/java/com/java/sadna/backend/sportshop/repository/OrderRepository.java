package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>,
        JpaSpecificationExecutor<OrderEntity> {

    // fetch user up front so the admin mapper's entity.getUser() doesn't fire N+1.
    @Override
    @EntityGraph(attributePaths = "user")
    Page<OrderEntity> findAll(Specification<OrderEntity> spec, Pageable pageable);

    // ON CONFLICT DO NOTHING keeps the order-number retry loop inside the same transaction --
    // a unique-violation on a raw INSERT would abort the PG transaction outright.
    @Modifying
    @Query(value = """
            INSERT INTO orders (
                user_id, status, total_price, order_number,
                shipping_full_name, shipping_email, shipping_phone,
                shipping_country, shipping_city, shipping_address_line
            ) VALUES (
                :userId, :status, :totalPrice, :orderNumber,
                :shippingFullName, :shippingEmail, :shippingPhone,
                :shippingCountry, :shippingCity, :shippingAddressLine
            )
            ON CONFLICT (order_number) DO NOTHING
            """, nativeQuery = true)
    int insertIfUniqueNumber(@Param("userId") Long userId,
                             @Param("status") String status,
                             @Param("totalPrice") BigDecimal totalPrice,
                             @Param("orderNumber") String orderNumber,
                             @Param("shippingFullName") String shippingFullName,
                             @Param("shippingEmail") String shippingEmail,
                             @Param("shippingPhone") String shippingPhone,
                             @Param("shippingCountry") String shippingCountry,
                             @Param("shippingCity") String shippingCity,
                             @Param("shippingAddressLine") String shippingAddressLine);

    @Query("SELECT o.id FROM OrderEntity o WHERE o.orderNumber = :orderNumber")
    Optional<Long> findIdByOrderNumber(@Param("orderNumber") String orderNumber);

    Optional<OrderEntity> findByOrderNumberAndUserId(String orderNumber, Long userId);

    // fetch user up front so the detail mapper's entity.getUser() doesn't fire a second query.
    @EntityGraph(attributePaths = "user")
    Optional<OrderEntity> findWithUserByOrderNumberAndUserId(String orderNumber, Long userId);

    @EntityGraph(attributePaths = "user")
    Optional<OrderEntity> findWithUserByOrderNumber(String orderNumber);

    // User + admin share this UPDATE; the role-conditional status target and WHERE gates are
    // inlined below. 0 rows means the caller lost the race against a concurrent status change.
    // clearAutomatically = true: the service pre-loads this row for the status pre-check, so
    // without it any post-update read of order.status / cancelled_at would return the stale
    // cached copy.
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE orders
               SET status       = CASE WHEN :isAdmin THEN 'CANCELLED_BY_ADMIN' ELSE 'CANCELLED_BY_USER' END,
                   cancelled_at = NOW(),
                   cancelled_by = :actorId,
                   updated_at   = NOW(),
                   updated_by   = :actorId
             WHERE id = :id
               AND (
                       (    :isAdmin AND status IN ('PAID', 'SHIPPED', 'DELIVERED'))
                    OR (NOT :isAdmin AND user_id = :actorId AND status = 'PAID')
                   )
            """, nativeQuery = true)
    int cancel(@Param("id") Long id,
               @Param("isAdmin") boolean isAdmin,
               @Param("actorId") Long actorId);
}
