package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.CartItemEntity;
import com.java.sadna.backend.sportshop.entity.id.CartItemId;
import com.java.sadna.backend.sportshop.model.CartViewRowDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, CartItemId> {

    long countByUserId(Long userId);

    @Modifying
    void deleteByUserId(Long userId);

    // Atomic guarded upsert. INSERT...SELECT enforces unarchived + size-exists + stock >=
    // requested via JOINs. On PK conflict, DO UPDATE uses `EXCLUDED` (Postgres pseudo-table
    // holding the proposed-insert row) to fold the new quantity into the existing one, and
    // re-reads stock so the increment only fires when new_total <= cap. 0 rows = guard
    // rejected (stock dropped under us, two tabs raced, etc.). No partial add.
    // clearAutomatically = true: addItem pre-loads product / stock / cart_item for stock pre-validation, so without it any future post-upsert read would return the stale cached copies.
    @Modifying(clearAutomatically = true)
    @Query(value = """
            INSERT INTO cart_items (user_id, product_id, size, quantity, product_version)
            SELECT :userId, :productId, :size, :requestedQty, p.version
              FROM products p
              JOIN product_stock ps ON ps.product_id = p.id AND ps.size = :size
             WHERE p.id          = :productId
               AND p.is_archived = false
               AND ps.quantity  >= :requestedQty
            ON CONFLICT (user_id, product_id, size) DO UPDATE
               SET quantity        = cart_items.quantity + EXCLUDED.quantity,
                   product_version = EXCLUDED.product_version
             WHERE (SELECT ps.quantity
                      FROM product_stock ps
                     WHERE ps.product_id = cart_items.product_id
                       AND ps.size       = cart_items.size)
                   >= cart_items.quantity + EXCLUDED.quantity
            """, nativeQuery = true)
    int upsertIfStockAllows(@Param("userId") Long userId,
                            @Param("productId") Long productId,
                            @Param("size") String size,
                            @Param("requestedQty") int requestedQty);

    // Ownership-gated conditional UPDATE. 0 rows -> service throws 404 (row doesn't belong
    // to this user or never existed). product_version is preserved on purpose -- the user
    // is changing quantity, not re-acknowledging the product.
    @Modifying
    @Query("""
            UPDATE CartItemEntity ci
               SET ci.quantity = :quantity
             WHERE ci.userId    = :userId
               AND ci.productId = :productId
               AND ci.size      = :size
            """)
    int updateQuantityByCompositeKey(@Param("userId") Long userId,
                                     @Param("productId") Long productId,
                                     @Param("size") String size,
                                     @Param("quantity") int quantity);

    // Composite-key conditional DELETE. 0 rows is treated as success (idempotent) so
    // duplicate clicks and two-tab DELETEs converge gracefully.
    @Modifying
    @Query("""
            DELETE FROM CartItemEntity ci
             WHERE ci.userId    = :userId
               AND ci.productId = :productId
               AND ci.size      = :size
            """)
    int deleteByCompositeKey(@Param("userId") Long userId,
                             @Param("productId") Long productId,
                             @Param("size") String size);

    // Bulk version-sync (the write half of POST /api/cart/sync). The `<>` predicate keeps the
    // common "nothing changed" case from generating any writes.
    @Modifying
    @Query(value = """
            UPDATE cart_items ci
               SET product_version = p.version
              FROM products p
             WHERE ci.product_id      = p.id
               AND ci.user_id         = :userId
               AND ci.product_version <> p.version
            """, nativeQuery = true)
    int bulkSyncVersionsByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT new com.java.sadna.backend.sportshop.model.CartViewRowDto(
                       ci.productId,
                       ci.size,
                       ci.quantity,
                       ci.productVersion,
                       p.id,
                       p.name,
                       p.imageFilename,
                       p.price,
                       p.archived,
                       p.version,
                       cat.name,
                       ps.quantity,
                       ps.lowStockThreshold)
              FROM CartItemEntity ci
              LEFT JOIN ProductEntity p ON p.id = ci.productId
              LEFT JOIN CategoryEntity cat ON cat.id = p.categoryId
              LEFT JOIN ProductStockEntity ps
                ON ps.productId = ci.productId AND ps.size = ci.size
             WHERE ci.userId = :userId
             ORDER BY ci.productId ASC, ci.size ASC
            """)
    List<CartViewRowDto> findCartViewRowsByUserId(@Param("userId") Long userId);
}
