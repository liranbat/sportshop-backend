package com.java.sadna.backend.sportshop.config;

public class ApiProperties {

    private final String pathPrefix;

    public ApiProperties(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }
}
