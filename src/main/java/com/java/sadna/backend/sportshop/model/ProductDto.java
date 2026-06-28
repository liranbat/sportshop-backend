package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ProductDto implements BaseDto {

    private final Long id;
    private final String name;
    private final String description;
    private final Long categoryId;
    private final boolean multiSize;
    private final String imageFilename;
    private final BigDecimal price;
    private final int version;
    private final boolean archived;
    private final OffsetDateTime updatedAt;
    private final Long updatedBy;
    private final OffsetDateTime archivedAt;
    private final Long archivedBy;

    public ProductDto(Long id,
                      String name,
                      String description,
                      Long categoryId,
                      boolean multiSize,
                      String imageFilename,
                      BigDecimal price,
                      int version,
                      boolean archived,
                      OffsetDateTime updatedAt,
                      Long updatedBy,
                      OffsetDateTime archivedAt,
                      Long archivedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.multiSize = multiSize;
        this.imageFilename = imageFilename;
        this.price = price;
        this.version = version;
        this.archived = archived;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.archivedAt = archivedAt;
        this.archivedBy = archivedBy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public boolean isMultiSize() {
        return multiSize;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getVersion() {
        return version;
    }

    public boolean isArchived() {
        return archived;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public OffsetDateTime getArchivedAt() {
        return archivedAt;
    }

    public Long getArchivedBy() {
        return archivedBy;
    }
}
