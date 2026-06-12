package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductStockRepository extends JpaRepository<ProductStockEntity, ProductStockId> {

    List<ProductStockEntity> findByProductId(Long productId);

    // Race-safe stock deduct used inside the checkout transaction. Atomically validates
    // stock + archive + version in one UPDATE -- 0 rows means the line lost the race (any
    // sub-cause: stock dropped, product archived, or version bumped). The caller treats
    // affected=0 as a single generic conflict and rolls back the whole checkout.
    @Modifying
    @Query(value = """
            UPDATE product_stock ps
               SET quantity = ps.quantity - :requestedQty
             WHERE ps.product_id = :productId
               AND ps.size       = :size
               AND ps.quantity  >= :requestedQty
               AND EXISTS (
                 SELECT 1 FROM products p
                  WHERE p.id          = ps.product_id
                    AND p.is_archived = false
                    AND p.version     = :expectedVersion
               )
            """, nativeQuery = true)
    int deductIfAvailable(@Param("productId") Long productId,
                          @Param("size") String size,
                          @Param("requestedQty") int requestedQty,
                          @Param("expectedVersion") int expectedVersion);

    // Stock restore inside the cancel transaction. No archive/version gate -- a sold line
    // gets its units back even if the product was archived or bumped since. 0 rows means
    // the product_stock row was deleted (e.g. size SKU retired) -- caller logs and skips.
    @Modifying
    @Query(value = """
            UPDATE product_stock
               SET quantity = quantity + :qty
             WHERE product_id = :productId
               AND size       = :size
            """, nativeQuery = true)
    int restore(@Param("productId") Long productId,
                @Param("size") String size,
                @Param("qty") int qty);
}
