package com.java.sadna.backend.sportshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

// audit columns (updated_at/_by, archived_at/_by) intentionally unmapped — JPA validate ignores extras
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

    @Column(name = "is_multi_size", nullable = false)
    private boolean multiSize;

    @Column(name = "image_filename", length = 200)
    private String imageFilename;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "is_archived", nullable = false)
    private boolean archived;

    protected ProductEntity() {
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
}
