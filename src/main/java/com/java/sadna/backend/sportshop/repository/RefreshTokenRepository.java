package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUserId(Long userId);

    // /auth/logout calls this; idempotent — returns 0 rows affected if no session exists.
    void deleteByUserId(Long userId);
}
