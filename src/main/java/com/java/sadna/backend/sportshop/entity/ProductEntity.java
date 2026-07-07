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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
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
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CategoryEntity category;

    @Column(name = "is_multi_size", nullable = false)
    private boolean multiSize;

    @Column(name = "image_filename", length = 200)
    private String imageFilename;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Version
    @Column(name = "version", nullable = false)
    @Setter(AccessLevel.NONE)
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
}
