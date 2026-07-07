package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class ProductDto {

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
}
