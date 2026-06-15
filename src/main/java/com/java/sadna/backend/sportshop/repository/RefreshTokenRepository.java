package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long>,
        JpaSpecificationExecutor<RefreshTokenEntity> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    // eager-fetch the user only for this query; everywhere else the association stays lazy.
    @EntityGraph(attributePaths = "user")
    @Override
    Page<RefreshTokenEntity> findAll(Specification<RefreshTokenEntity> spec, Pageable pageable);

    @Modifying
    @Query("""
            DELETE FROM RefreshTokenEntity rt
             WHERE rt.id      = :id
               AND rt.userId <> :actorId
            """)
    int deleteByIdExcludingActor(@Param("id") Long id,
                                 @Param("actorId") Long actorId);

    @Modifying
    @Query("""
            DELETE FROM RefreshTokenEntity rt
             WHERE rt.userId <> :actorId
            """)
    int deleteAllExceptActor(@Param("actorId") Long actorId);
}
