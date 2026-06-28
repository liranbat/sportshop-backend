package com.java.sadna.backend.sportshop.model.enums;

// Image formats we accept. Detected from the file's magic bytes, not from
// the multipart Content-Type header (which a client can lie about).
public enum DetectedImageType {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp"),
    AVIF("image/avif", "avif"),
    SVG("image/svg+xml", "svg");

    private final String mime;
    private final String extension;

    DetectedImageType(String mime, String extension) {
        this.mime = mime;
        this.extension = extension;
    }

    public String getMime() {
        return mime;
    }

    public String getExtension() {
        return extension;
    }

    public static DetectedImageType fromMime(String mime) {
        if (mime == null) {
            return null;
        }
        for (DetectedImageType type : values()) {
            if (type.mime.equalsIgnoreCase(mime)) {
                return type;
            }
        }
        return null;
    }
}
