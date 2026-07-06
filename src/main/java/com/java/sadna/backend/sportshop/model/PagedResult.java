package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PagedResult<T> {

    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
}
