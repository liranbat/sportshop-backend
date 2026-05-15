package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByDeletedFalseOrderByNameAsc();
}
