package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

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
}
