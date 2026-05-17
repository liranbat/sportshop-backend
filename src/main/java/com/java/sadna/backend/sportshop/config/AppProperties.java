package com.java.sadna.backend.sportshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final CorsProperties cors;
    private final ImagesProperties images;
    private final AuthProperties auth;

    public AppProperties(CorsProperties cors, ImagesProperties images, AuthProperties auth) {
        this.cors = cors;
        this.images = images;
        this.auth = auth;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public ImagesProperties getImages() {
        return images;
    }

    public AuthProperties getAuth() {
        return auth;
    }
}
