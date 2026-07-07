package com.java.sadna.backend.sportshop.common.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DatesUtil {

    private DatesUtil() {
    }

    public static OffsetDateTime utcStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime utcStartOfNextDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
