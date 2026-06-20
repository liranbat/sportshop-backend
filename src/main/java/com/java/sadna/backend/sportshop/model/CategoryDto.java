package com.java.sadna.backend.sportshop.model;

import java.time.OffsetDateTime;

public class CategoryDto implements BaseDto {

    private final Long id;
    private final String name;
    private final String iconFilename;
    private final boolean deleted;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime deletedAt;
    private final Long deletedBy;
    private final OffsetDateTime updatedAt;
    private final Long updatedBy;

    public CategoryDto(Long id,
                       String name,
                       String iconFilename,
                       boolean deleted,
                       OffsetDateTime createdAt,
                       OffsetDateTime deletedAt,
                       Long deletedBy,
                       OffsetDateTime updatedAt,
                       Long updatedBy) {
        this.id = id;
        this.name = name;
        this.iconFilename = iconFilename;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
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

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }
}
