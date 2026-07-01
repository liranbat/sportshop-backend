package com.java.sadna.backend.sportshop.model.enums;

import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.exception.BadRequestException;

import java.util.EnumSet;
import java.util.Set;

// Per-resource upload policy: URL segment, allowed filename extensions, allowed
// detected image types, and max file size.
public enum ResourceImagePolicy {

    PRODUCTS(
            "products",
            EnumSet.of(
                    DetectedImageType.JPEG,
                    DetectedImageType.PNG,
                    DetectedImageType.WEBP,
                    DetectedImageType.AVIF
            ),
            Set.of("jpg", "jpeg", "png", "webp", "avif"),
            12L * 1024L * 1024L
    ),
    CATEGORIES(
            "categories",
            EnumSet.of(DetectedImageType.SVG),
            Set.of("svg"),
            12L * 1024L * 1024L
    );

    private final String urlSegment;
    private final Set<DetectedImageType> allowedTypes;
    private final Set<String> allowedExtensions;
    private final long maxBytes;

    ResourceImagePolicy(String urlSegment,
                        Set<DetectedImageType> allowedTypes,
                        Set<String> allowedExtensions,
                        long maxBytes) {
        this.urlSegment = urlSegment;
        this.allowedTypes = allowedTypes;
        this.allowedExtensions = allowedExtensions;
        this.maxBytes = maxBytes;
    }

    public String getUrlSegment() {
        return urlSegment;
    }

    public Set<DetectedImageType> getAllowedTypes() {
        return allowedTypes;
    }

    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public boolean allows(DetectedImageType type) {
        return type != null && allowedTypes.contains(type);
    }

    public String subdirIn(ImagesProperties images) {
        return switch (this) {
            case PRODUCTS -> images.getProductPrefix();
            case CATEGORIES -> images.getCategoryPrefix();
        };
    }

    public static ResourceImagePolicy fromUrlSegment(String segment) {
        if (segment != null) {
            for (ResourceImagePolicy policy : values()) {
                if (policy.urlSegment.equals(segment)) {
                    return policy;
                }
            }
        }
        throw new BadRequestException("http.badRequest.typeMismatch", segment, "resourceType");
    }
}
