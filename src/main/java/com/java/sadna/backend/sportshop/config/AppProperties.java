package com.java.sadna.backend.sportshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final ApiProperties api;
    private final CorsProperties cors;
    private final ImagesProperties images;
    private final AuthProperties auth;

    public AppProperties(
            ApiProperties api,
            CorsProperties cors,
            ImagesProperties images,
            AuthProperties auth) {
        this.api = api;
        this.cors = cors;
        this.images = images;
        this.auth = auth;
    }

    public ApiProperties getApi() {
        return api;
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
