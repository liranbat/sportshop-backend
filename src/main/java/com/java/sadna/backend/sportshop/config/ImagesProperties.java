package com.java.sadna.backend.sportshop.config;

// bound recursively by Spring from AppProperties — no @ConfigurationProperties of its own
public class ImagesProperties {

    private final String urlPrefix;
    private final String categoryPrefix;
    private final String productPrefix;
    private final String localDir;

    public ImagesProperties(String urlPrefix, String categoryPrefix, String productPrefix, String localDir) {
        this.urlPrefix = urlPrefix;
        this.categoryPrefix = categoryPrefix;
        this.productPrefix = productPrefix;
        this.localDir = localDir;
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

    // composes the icon URL from the slash-free prefixes (e.g. "/images/categories/soccer.svg"). null-safe.
    public String getCategoryIconUrl(String filename) {
        return filename == null ? null : "/" + urlPrefix + "/" + categoryPrefix + "/" + filename;
    }
}
