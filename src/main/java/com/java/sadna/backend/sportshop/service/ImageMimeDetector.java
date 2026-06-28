package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.model.enums.DetectedImageType;
import org.apache.tika.Tika;

// Tika validates the upload bytes and returns the format; null = not in our allow-list.
public final class ImageMimeDetector {

    private static final Tika TIKA = new Tika();

    private ImageMimeDetector() {
    }

    public static DetectedImageType detect(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return DetectedImageType.fromMime(TIKA.detect(bytes));
    }
}
