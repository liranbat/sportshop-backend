package com.java.sadna.backend.sportshop.common.util;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SequencedMap;

// Resolves (sortField, sortDirection) into a Spring Data Sort. Both absent -> defaultSort.
// Anything else validates strictly: unknown sortField, missing/invalid sortDirection, or
// sortDirection-without-sortField all throw BadRequestException. Known sortField expands
// to one or more JPA properties (sharing the caller's direction) with tiebreakers appended.
// Build the SequencedMap<property, direction> literals via SortResolver.orders(k, v, ...).
public final class SortResolver {

    // Case-insensitive: sortField -> one or more JPA property paths (multi-property
    // fan-out for a single sortField shares the caller's direction across all entries).
    private final Map<String, List<String>> fieldToProperties;
    // Returned when both sortField and sortDirection are absent.
    private final List<Sort.Order> defaultSort;
    // Appended after the primary Sort.Order(s); a tiebreak matching any primary is skipped.
    private final List<Sort.Order> tiebreakers;

    public SortResolver(Map<String, List<String>> fieldToProperties,
                        SequencedMap<String, String> defaultSort,
                        SequencedMap<String, String> tiebreakers) {
        List<Sort.Order> parsedDefault = toOrders(defaultSort);
        if (parsedDefault.isEmpty()) {
            throw new IllegalArgumentException("SortResolver requires a non-empty defaultSort");
        }
        this.fieldToProperties = normalizeKeys(fieldToProperties);
        this.defaultSort = parsedDefault;
        this.tiebreakers = toOrders(tiebreakers);
    }

    public Sort resolve(String sortField, String sortDirection) {
        boolean hasField = sortField != null && !sortField.isBlank();
        boolean hasDirection = sortDirection != null && !sortDirection.isBlank();
        if (!hasField && !hasDirection) {
            return Sort.by(defaultSort);
        }
        if (!hasField) {
            throw new BadRequestException(ErrorConstants.Http.BAD_REQUEST_SORT_FIELD_REQUIRED);
        }
        List<String> properties = fieldToProperties.get(sortField.toLowerCase(Locale.ROOT));
        if (properties == null || properties.isEmpty()) {
            throw new BadRequestException(ErrorConstants.Http.BAD_REQUEST_UNKNOWN_SORT_FIELD, sortField);
        }
        Sort.Direction dir;
        try {
            dir = SortDirections.parse(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorConstants.Http.BAD_REQUEST_INVALID_SORT_DIRECTION, sortDirection);
        }
        List<Sort.Order> orders = new ArrayList<>(properties.size() + tiebreakers.size());
        for (String property : properties) {
            orders.add(new Sort.Order(dir, property));
        }
        Set<String> primaryProps = new HashSet<>(properties);
        for (Sort.Order tb : tiebreakers) {
            if (!primaryProps.contains(tb.getProperty())) {
                orders.add(tb);
            }
        }
        return Sort.by(orders);
    }

    // Alternating (property, direction) string pairs -> insertion-ordered map. Empty pairs
    // is a valid empty spec (tiebreakers only). Duplicate properties or odd arg count throw.
    public static SequencedMap<String, String> orders(String... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "SortResolver.orders() requires alternating property/direction pairs; got "
                            + pairs.length + " arg(s)");
        }
        LinkedHashMap<String, String> out = new LinkedHashMap<>(pairs.length / 2);
        for (int i = 0; i < pairs.length; i += 2) {
            if (out.put(pairs[i], pairs[i + 1]) != null) {
                throw new IllegalArgumentException(
                        "SortResolver.orders() got duplicate property: " + pairs[i]);
            }
        }
        return out;
    }

    private static List<Sort.Order> toOrders(SequencedMap<String, String> spec) {
        if (spec == null || spec.isEmpty()) return List.of();
        List<Sort.Order> out = new ArrayList<>(spec.size());
        for (Map.Entry<String, String> e : spec.entrySet()) {
            out.add(new Sort.Order(SortDirections.parse(e.getValue()), e.getKey()));
        }
        return List.copyOf(out);
    }

    private static Map<String, List<String>> normalizeKeys(Map<String, List<String>> raw) {
        Map<String, List<String>> out = new HashMap<>();
        raw.forEach((k, v) -> out.put(k.toLowerCase(Locale.ROOT), List.copyOf(v)));
        return Map.copyOf(out);
    }
}
