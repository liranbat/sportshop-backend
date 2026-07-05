package com.java.sadna.backend.sportshop.common.util;

import org.springframework.data.domain.Sort;

// Case-insensitive sortDirection parser. Throws IllegalArgumentException on unknown input.
public final class SortDirections {

    public static final String ASC = "asc";
    public static final String DESC = "desc";

    private SortDirections() {
    }

    public static Sort.Direction parse(String sortDirection) {
        if (ASC.equalsIgnoreCase(sortDirection)) return Sort.Direction.ASC;
        if (DESC.equalsIgnoreCase(sortDirection)) return Sort.Direction.DESC;
        throw new IllegalArgumentException(
                "Unknown sort direction: '" + sortDirection + "' (expected 'asc' or 'desc')");
    }
}
