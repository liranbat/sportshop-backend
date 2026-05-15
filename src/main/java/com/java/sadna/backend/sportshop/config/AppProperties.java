package com.java.sadna.backend.sportshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final CorsProperties cors;
    private final ImagesProperties images;

    public AppProperties(CorsProperties cors, ImagesProperties images) {
        this.cors = cors;
        this.images = images;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public ImagesProperties getImages() {
        return images;
    }
}
