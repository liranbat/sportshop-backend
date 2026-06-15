package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    boolean existsByEmail(String email);

    long countByAdminTrueAndDeletedFalse();

    // deleted=false guard skipped when the actor is an admin -- admins can edit soft-deleted users
    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.firstName = :firstName,
                   u.lastName  = :lastName,
                   u.phone     = :phone,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id = :userId
               AND (:actorIsAdmin = true OR u.deleted = false)
            """)
    int applyProfileEdit(@Param("userId") Long userId,
                         @Param("firstName") String firstName,
                         @Param("lastName") String lastName,
                         @Param("phone") String phone,
                         @Param("actorId") Long actorId,
                         @Param("now") OffsetDateTime now,
                         @Param("actorIsAdmin") boolean actorIsAdmin);

    // clearAutomatically = true: changePassword pre-loads this row for the password check, so without it any future post-update read would return the stale cached copy.
    @Modifying(clearAutomatically = true)
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

    // last-admin guard: the only remaining active admin can't self-delete
    // clearAutomatically = true: deleteAccount pre-loads this row for the password + last-admin checks, so without it any future post-update read would return the stale cached copy.
    @Modifying(clearAutomatically = true)
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
               AND (u.admin = false
                    OR (SELECT COUNT(a) FROM UserEntity a WHERE a.admin = true AND a.deleted = false) > 1)
            """)
    int softDelete(@Param("userId") Long userId,
                   @Param("expectedHash") String expectedHash,
                   @Param("now") OffsetDateTime now);

    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.admin     = true,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id        = :userId
               AND u.admin     = false
               AND u.deleted   = false
            """)
    int applyPromote(@Param("userId") Long userId,
                     @Param("actorId") Long actorId,
                     @Param("now") OffsetDateTime now);

    // count > 1 subquery is the atomic last-admin guard: the only remaining
    // active admin cannot be demoted (would leave the system with zero admins)
    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.admin     = false,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id        = :userId
               AND u.admin     = true
               AND u.deleted   = false
               AND (SELECT COUNT(a) FROM UserEntity a WHERE a.admin = true AND a.deleted = false) > 1
            """)
    int applyDemote(@Param("userId") Long userId,
                    @Param("actorId") Long actorId,
                    @Param("now") OffsetDateTime now);

    // last-admin guard mirrors applyDemote: an admin (self or another) cannot be
    // soft-deleted if doing so would leave the system with zero active admins.
    // clearAutomatically = true: the service pre-loads this row for the pre-check, so without it the post-update reload would return the stale cached copy (deleted = false) instead of fresh DB data.
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE UserEntity u
               SET u.deleted   = true,
                   u.deletedAt = :now,
                   u.deletedBy = :actorId,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id        = :userId
               AND u.deleted   = false
               AND (u.admin = false
                    OR (SELECT COUNT(a) FROM UserEntity a WHERE a.admin = true AND a.deleted = false) > 1)
            """)
    int applyAdminSoftDelete(@Param("userId") Long userId,
                             @Param("actorId") Long actorId,
                             @Param("now") OffsetDateTime now);

    @Modifying
    @Query("""
            UPDATE UserEntity u
               SET u.deleted   = false,
                   u.deletedAt = null,
                   u.deletedBy = null,
                   u.updatedAt = :now,
                   u.updatedBy = :actorId
             WHERE u.id        = :userId
               AND u.deleted   = true
            """)
    int applyAdminRestore(@Param("userId") Long userId,
                          @Param("actorId") Long actorId,
                          @Param("now") OffsetDateTime now);
}
