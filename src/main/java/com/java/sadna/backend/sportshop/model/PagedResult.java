package com.java.sadna.backend.sportshop.model;

import java.util.List;

public class PagedResult<T> {

    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public PagedResult(List<T> items,
                       int page,
                       int pageSize,
                       long totalElements,
                       int totalPages) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
