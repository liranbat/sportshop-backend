package com.java.sadna.backend.sportshop.config;

public class ImagesProperties {

    private final String publicBaseUrl;
    private final String urlPrefix;
    private final String categoryPrefix;
    private final String productPrefix;
    private final String localDir;
    private final long cacheTtlSeconds;

    public ImagesProperties(String publicBaseUrl, String urlPrefix, String categoryPrefix,
                            String productPrefix, String localDir, long cacheTtlSeconds) {
        this.publicBaseUrl = stripTrailingSlash(publicBaseUrl);
        this.urlPrefix = urlPrefix;
        this.categoryPrefix = categoryPrefix;
        this.productPrefix = productPrefix;
        this.localDir = localDir;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
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

    // Composes the absolute icon URL (e.g. "http://localhost:8080/images/categories/soccer.svg"). Null-safe.
    public String getCategoryIconUrl(String filename) {
        return composeUrl(categoryPrefix, filename);
    }

    // Composes the absolute product image URL (e.g. "http://localhost:8080/images/products/basketball-spalding.jpg"). Null-safe.
    public String getProductImageUrl(String filename) {
        return composeUrl(productPrefix, filename);
    }

    // Generic composer for callers that already know the subdir (e.g. upload service).
    public String composeUrl(String subdir, String filename) {
        return filename == null ? null : publicBaseUrl + "/" + urlPrefix + "/" + subdir + "/" + filename;
    }

    // URL-path prefix (no host) for matching any image request, e.g. "/images/".
    public String getUrlPathPrefix() {
        return "/" + urlPrefix + "/";
    }

    // URL-path prefix (no host) for matching a category-image request, e.g. "/images/categories/".
    public String getCategoryPathPrefix() {
        return "/" + urlPrefix + "/" + categoryPrefix + "/";
    }

    private static String stripTrailingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
