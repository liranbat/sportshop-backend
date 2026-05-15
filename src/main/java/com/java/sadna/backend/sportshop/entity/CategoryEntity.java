package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// audit columns (updated_at/_by, deleted_at/_by) intentionally unmapped — JPA validate ignores extras
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "icon_filename", length = 100)
    private String iconFilename;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    protected CategoryEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconFilename() {
        return iconFilename;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
