package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class CategoryDto {

    private final Long id;
    private final String name;
    private final String iconFilename;
    private final boolean deleted;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime deletedAt;
    private final Long deletedBy;
    private final OffsetDateTime updatedAt;
    private final Long updatedBy;
}
