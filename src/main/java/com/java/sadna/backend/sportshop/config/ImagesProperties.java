package com.java.sadna.backend.sportshop.config;

import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.images")
@Validated
public class ImagesProperties {

    private final String urlPrefix;
    private final String categoryPrefix;
    private final String productPrefix;
    private final String localDir;
    private final long cacheTtlSeconds;
    private final DataSize maxUploadBytes;
    private final int uuidRetries;

    public ImagesProperties(
            @NotBlank String urlPrefix,
            @NotBlank String categoryPrefix,
            @NotBlank String productPrefix,
            @NotBlank String localDir,
            @Min(0) long cacheTtlSeconds,
            @DefaultValue("12MB") @NotNull DataSize maxUploadBytes,
            @DefaultValue("5") @Min(1) int uuidRetries) {
        this.urlPrefix = urlPrefix;
        this.categoryPrefix = categoryPrefix;
        this.productPrefix = productPrefix;
        this.localDir = localDir;
        this.cacheTtlSeconds = cacheTtlSeconds;
        this.maxUploadBytes = maxUploadBytes;
        this.uuidRetries = uuidRetries;
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

    public DataSize getMaxUploadBytes() {
        return maxUploadBytes;
    }

    public int getUuidRetries() {
        return uuidRetries;
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

    // Same as parseFilename but converts the IllegalArgumentException into a 400 with the
    // caller-supplied message key. Used by ProductService and CategoryService so their
    // try/catch wrappers stay one line.
    public String parseFilenameOrBadRequest(ResourceImagePolicy policy, String url, String badRequestMessageKey) {
        try {
            return parseFilename(policy, url);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(badRequestMessageKey);
        }
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
