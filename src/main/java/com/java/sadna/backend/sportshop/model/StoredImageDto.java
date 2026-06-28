package com.java.sadna.backend.sportshop.model;

public class StoredImageDto implements BaseDto {

    private final String filename;
    private final String url;

    public StoredImageDto(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public String getUrl() {
        return url;
    }
}
