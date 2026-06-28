package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by", insertable = false, updatable = false)
    private Long updatedBy;

    @Column(name = "deleted_at", insertable = false, updatable = false)
    private OffsetDateTime deletedAt;

    @Column(name = "deleted_by", insertable = false, updatable = false)
    private Long deletedBy;

    protected CategoryEntity() {
    }

    public CategoryEntity(String name, String iconFilename) {
        this.name = name;
        this.iconFilename = iconFilename;
        this.deleted = false;
        this.createdAt = OffsetDateTime.now();
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }
}
