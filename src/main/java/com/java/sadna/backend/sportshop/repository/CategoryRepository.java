package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // share-locks the row for the rest of the TX so another admin can't mutate it until we commit.
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    Optional<CategoryEntity> findByIdWithLock(@Param("id") Long id);

    // exclusive-locks the row; softDelete grabs this BEFORE bulkReassign to avoid the
    // X-on-products + X-on-category deadlock with concurrent product updates.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    Optional<CategoryEntity> findByIdWithWriteLock(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE CategoryEntity c
               SET c.name         = :name,
                   c.iconFilename = :iconFilename,
                   c.updatedAt    = :now,
                   c.updatedBy    = :actorId
             WHERE c.id           = :id
            """)
    int applyEdit(@Param("id") Long id,
                  @Param("name") String name,
                  @Param("iconFilename") String iconFilename,
                  @Param("actorId") Long actorId,
                  @Param("now") OffsetDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE CategoryEntity c
               SET c.deleted   = true,
                   c.deletedAt = :now,
                   c.deletedBy = :actorId,
                   c.updatedAt = :now,
                   c.updatedBy = :actorId
             WHERE c.id        = :id
               AND c.deleted   = false
            """)
    int applySoftDelete(@Param("id") Long id,
                        @Param("actorId") Long actorId,
                        @Param("now") OffsetDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE CategoryEntity c
               SET c.deleted   = false,
                   c.deletedAt = null,
                   c.deletedBy = null,
                   c.updatedAt = :now,
                   c.updatedBy = :actorId
             WHERE c.id        = :id
               AND c.deleted   = true
            """)
    int applyRestore(@Param("id") Long id,
                     @Param("actorId") Long actorId,
                     @Param("now") OffsetDateTime now);
}
