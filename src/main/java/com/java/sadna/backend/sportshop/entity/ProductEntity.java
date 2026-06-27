package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    // Read-only association mapped solely so JPA queries can ORDER BY p.category.name.
    // Do NOT add a getter or navigate via Java — the scalar categoryId above stays the single
    // source of truth for the FK; insertable/updatable=false keeps Hibernate from treating
    // this as a second writer to the category_id column.
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @Column(name = "is_multi_size", nullable = false)
    private boolean multiSize;

    @Column(name = "image_filename", length = 200)
    private String imageFilename;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Version
    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "is_archived", nullable = false)
    private boolean archived;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;

    @Column(name = "archived_by")
    private Long archivedBy;

    protected ProductEntity() {
    }

    public ProductEntity(String name,
                         String description,
                         Long categoryId,
                         boolean multiSize,
                         String imageFilename,
                         BigDecimal price) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.multiSize = multiSize;
        this.imageFilename = imageFilename;
        this.price = price;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setMultiSize(boolean multiSize) {
        this.multiSize = multiSize;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public void setArchivedBy(Long archivedBy) {
        this.archivedBy = archivedBy;
    }
}
