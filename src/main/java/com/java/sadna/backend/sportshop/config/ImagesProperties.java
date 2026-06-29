package com.java.sadna.backend.sportshop.config;

import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;

public class ImagesProperties {

    private final String urlPrefix;
    private final String categoryPrefix;
    private final String productPrefix;
    private final String localDir;
    private final long cacheTtlSeconds;

    public ImagesProperties(String urlPrefix, String categoryPrefix,
                            String productPrefix, String localDir, long cacheTtlSeconds) {
        this.urlPrefix = urlPrefix;
        this.categoryPrefix = categoryPrefix;
        this.productPrefix = productPrefix;
        this.localDir = localDir;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public String getCategoryPrefix() {
        return categoryPrefix;
    }

    public String getProductPrefix() {
        return productPrefix;
    }

    public String getLocalDir() {
        return localDir;
    }

    public long getCacheTtlSeconds() {
        return cacheTtlSeconds;
    }

    // Composes the relative icon URL (e.g. "/images/categories/soccer.svg"). Null-safe.
    public String getCategoryIconUrl(String filename) {
        return composeUrl(categoryPrefix, filename);
    }

    // Composes the relative product image URL (e.g. "/images/products/basketball-spalding.jpg"). Null-safe.
    public String getProductImageUrl(String filename) {
        return composeUrl(productPrefix, filename);
    }

    // Generic composer for callers that already know the subdir (e.g. upload service).
    public String composeUrl(String subdir, String filename) {
        return filename == null ? null : "/" + urlPrefix + "/" + subdir + "/" + filename;
    }

    public String parseFilename(ResourceImagePolicy policy, String url) {
        if (url == null) {
            return null;
        }
        String expectedPrefix = "/" + urlPrefix + "/" + policy.subdirIn(this) + "/";
        if (!url.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException(
                    "URL does not match expected prefix " + expectedPrefix);
        }
        String filename = url.substring(expectedPrefix.length());
        if (filename.isEmpty() || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException(
                    "URL has empty or nested filename suffix after prefix");
        }
        return filename;
    }

    // URL-path prefix (no host) for matching any image request, e.g. "/images/".
    public String getUrlPathPrefix() {
        return "/" + urlPrefix + "/";
    }

    // URL-path prefix (no host) for matching a category-image request, e.g. "/images/categories/".
    public String getCategoryPathPrefix() {
        return "/" + urlPrefix + "/" + categoryPrefix + "/";
    }
}
