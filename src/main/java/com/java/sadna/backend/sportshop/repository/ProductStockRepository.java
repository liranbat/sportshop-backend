package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductStockRepository
        extends JpaRepository<ProductStockEntity, ProductStockId>,
                JpaSpecificationExecutor<ProductStockEntity> {

    List<ProductStockEntity> findByProductId(Long productId);

    // Native -- JPQL has no `INSERT ... VALUES`. Duplicate hits the unique constraint.
    @Modifying
    @Query(value = """
            INSERT INTO product_stock (product_id, size, quantity, low_stock_threshold)
            VALUES (:productId, :size, :quantity, :threshold)
            """, nativeQuery = true)
    int adminInsert(@Param("productId") Long productId,
                    @Param("size") String size,
                    @Param("quantity") int quantity,
                    @Param("threshold") Integer threshold);

    // Bulk delete used by the isMultiSize-flip cascade during ProductService.update — wipes all
    // per-size rows in a single statement; the caller then re-inserts the single ONE_SIZE row
    // when the new mode is single-size.
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProductStockEntity ps WHERE ps.productId = :productId")
    int deleteAllByProductId(@Param("productId") Long productId);

    // Race-safe stock deduct used inside the checkout transaction. Atomically validates
    // stock + archive + version in one UPDATE -- 0 rows means the line lost the race (any
    // sub-cause: stock dropped, product archived, or version bumped). The caller treats
    // affected=0 as a single generic conflict and rolls back the whole checkout.
    @Modifying
    @Query("""
            UPDATE ProductStockEntity ps
               SET ps.quantity = ps.quantity - :requestedQty
             WHERE ps.productId = :productId
               AND ps.size       = :size
               AND ps.quantity  >= :requestedQty
               AND EXISTS (
                 SELECT 1 FROM ProductEntity p
                  WHERE p.id       = ps.productId
                    AND p.archived = false
                    AND p.version  = :expectedVersion
               )
            """)
    int deductIfAvailable(@Param("productId") Long productId,
                          @Param("size") String size,
                          @Param("requestedQty") int requestedQty,
                          @Param("expectedVersion") int expectedVersion);

    // Stock restore inside the cancel transaction. No archive/version gate -- a sold line
    // gets its units back even if the product was archived or bumped since. 0 rows means
    // the product_stock row was deleted (e.g. size SKU retired) -- caller logs and skips.
    @Modifying
    @Query("""
            UPDATE ProductStockEntity ps
               SET ps.quantity = ps.quantity + :qty
             WHERE ps.productId = :productId
               AND ps.size       = :size
            """)
    int restore(@Param("productId") Long productId,
                @Param("size") String size,
                @Param("qty") int qty);

    // Admin row-save: sets quantity + threshold absolute. 0 rows = row gone -> caller maps to 404.
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE ProductStockEntity ps
               SET ps.quantity           = :quantity,
                   ps.lowStockThreshold  = :threshold
             WHERE ps.productId = :productId
               AND ps.size       = :size
            """)
    int adminSet(@Param("productId") Long productId,
                 @Param("size") String size,
                 @Param("quantity") int quantity,
                 @Param("threshold") Integer threshold);

    // Admin +/- adjust with the non-negative safety rail. 0 rows = row gone OR rail fired;
    // caller throws a single 409.
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE ProductStockEntity ps
               SET ps.quantity = ps.quantity + :delta
             WHERE ps.productId = :productId
               AND ps.size       = :size
               AND ps.quantity + :delta >= 0
            """)
    int adminAdjust(@Param("productId") Long productId,
                    @Param("size") String size,
                    @Param("delta") int delta);

    // Admin remove-size. Idempotent -- 0 rows == already removed, treated as success by caller.
    @Modifying(clearAutomatically = true)
    @Query("""
            DELETE FROM ProductStockEntity ps
             WHERE ps.productId = :productId
               AND ps.size       = :size
            """)
    int adminDelete(@Param("productId") Long productId,
                    @Param("size") String size);
}
