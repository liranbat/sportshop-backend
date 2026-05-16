package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    // existsByEmail (not existsByEmailAndDeletedFalse): emails stay reserved
    // across soft-delete per project-summary §3.10, so register's "email
    // already taken" check must still trip on soft-deleted rows.
    boolean existsByEmail(String email);
}
