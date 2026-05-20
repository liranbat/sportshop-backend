package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    boolean existsByEmail(String email);

    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.firstName = :firstName,
                   u.lastName  = :lastName,
                   u.phone     = :phone,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id      = :userId
               AND u.deleted = false
            """)
    int applyProfileEdit(@Param("userId") Long userId,
                         @Param("firstName") String firstName,
                         @Param("lastName") String lastName,
                         @Param("phone") String phone,
                         @Param("actorId") Long actorId,
                         @Param("now") OffsetDateTime now);

    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.passwordHash = :newHash,
                   u.updatedAt    = :now,
                   u.updatedBy    = :actorId
             WHERE u.id           = :userId
               AND u.passwordHash = :expectedHash
               AND u.deleted      = false
            """)
    int rotatePassword(@Param("userId") Long userId,
                       @Param("expectedHash") String expectedHash,
                       @Param("newHash") String newHash,
                       @Param("actorId") Long actorId,
                       @Param("now") OffsetDateTime now);

    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.deleted   = true,
                   u.deletedAt = :now,
                   u.deletedBy = :userId,
                   u.updatedAt = :now,
                   u.updatedBy = :userId
             WHERE u.id           = :userId
               AND u.passwordHash = :expectedHash
               AND u.deleted      = false
            """)
    int softDelete(@Param("userId") Long userId,
                   @Param("expectedHash") String expectedHash,
                   @Param("now") OffsetDateTime now);
}
