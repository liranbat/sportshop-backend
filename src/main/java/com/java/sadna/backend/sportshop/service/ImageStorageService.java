package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.InternalServerErrorException;
import com.java.sadna.backend.sportshop.exception.PayloadTooLargeException;
import com.java.sadna.backend.sportshop.model.StoredImageDto;
import com.java.sadna.backend.sportshop.model.enums.DetectedImageType;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import com.java.sadna.backend.sportshop.util.SvgSecurityScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

// Validates an uploaded image and writes it to {local-dir}/{subdir}/{uuid}.{ext}.
// Magic bytes -- not the multipart Content-Type header -- decide the format.
@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    // Bounded retry on the (vanishingly rare) UUID collision case.
    private static final int MAX_KEY_ATTEMPTS = 5;

    private final AppProperties appProperties;

    public ImageStorageService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public StoredImageDto store(MultipartFile file, ResourceImagePolicy policy) {
        requireFile(file);
        requireAllowedExtension(file.getOriginalFilename(), policy);

        byte[] bytes = readBytes(file);
        DetectedImageType detected = ImageMimeDetector.detect(bytes);
        if (detected == null) {
            throw new BadRequestException("Uploaded file is not a recognizable image.");
        }
        if (!policy.allows(detected)) {
            throw new BadRequestException(
                    "File type " + detected.getMime() + " is not allowed for this resource."
            );
        }
        if (bytes.length > policy.getMaxBytes()) {
            throw new PayloadTooLargeException("File exceeds the allowed size for this resource.");
        }
        if (detected == DetectedImageType.SVG && SvgSecurityScanner.isUnsafe(bytes)) {
            throw new BadRequestException(
                    "SVG contains script content or event-handler attributes."
            );
        }

        String subdir = policy.subdirIn(appProperties.getImages());
        Path target = writeWithKeyRetry(bytes, subdir, detected);
        log.info("Stored uploaded image at {}", target);

        String filename = target.getFileName().toString();
        String url = appProperties.getImages().composeUrl(subdir, filename);
        return new StoredImageDto(filename, url);
    }

    private static void requireFile(MultipartFile file) {
        if (file == null) {
            throw new BadRequestException("Multipart 'file' part is required.");
        }
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty.");
        }
    }

    private static void requireAllowedExtension(String original, ResourceImagePolicy policy) {
        Set<String> allowed = policy.getAllowedExtensions();
        String ext = lowerExt(original);
        if (ext == null || !allowed.contains(ext)) {
            throw new BadRequestException(
                    "Filename must have one of: " + String.join(", ", allowed)
            );
        }
    }

    private static String lowerExt(String filename) {
        if (filename == null) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String newFilename(DetectedImageType detected) {
        return UUID.randomUUID() + "." + detected.getExtension();
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new InternalServerErrorException("Could not read uploaded file.");
        }
    }

    private Path writeWithKeyRetry(byte[] bytes, String resourceSubdir, DetectedImageType detected) {
        Path dir = Path.of(appProperties.getImages().getLocalDir(), resourceSubdir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Failed to prepare image directory {}", dir, e);
            throw new InternalServerErrorException("Could not store the uploaded image.");
        }

        for (int attempt = 1; attempt <= MAX_KEY_ATTEMPTS; attempt++) {
            String filename = newFilename(detected);
            Path target = dir.resolve(filename).normalize();
            if (!target.startsWith(dir)) {
                throw new InternalServerErrorException("Resolved storage path escapes the image directory.");
            }
            try {
                Files.write(target, bytes, StandardOpenOption.CREATE_NEW);
                return target;
            } catch (FileAlreadyExistsException collision) {
                log.warn("UUID collision on attempt {}/{} for {}; regenerating", attempt, MAX_KEY_ATTEMPTS, target);
            } catch (IOException e) {
                log.error("Failed to write uploaded image to {}", target, e);
                throw new InternalServerErrorException("Could not store the uploaded image.");
            }
        }
        log.error("Exhausted {} UUID attempts under {}", MAX_KEY_ATTEMPTS, dir);
        throw new InternalServerErrorException("Could not allocate a unique storage key after several attempts.");
    }
}
