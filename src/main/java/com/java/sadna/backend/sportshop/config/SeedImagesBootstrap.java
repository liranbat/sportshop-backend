package com.java.sadna.backend.sportshop.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// Copies seed images from the JAR into ${app.images.local-dir} on startup.
// @PostConstruct (not ApplicationRunner) so the copy completes BEFORE Tomcat
// accepts traffic — otherwise /images/* would 404 briefly on first boot.
@Component
@Slf4j
public class SeedImagesBootstrap {

    private static final String SEED_PATTERN = "classpath:seed-images/**/*.*";
    private static final String SEED_ROOT_MARKER = "seed-images/";

    private final ImagesProperties images;
    private final ResourcePatternResolver resolver;

    public SeedImagesBootstrap(ImagesProperties images, ResourcePatternResolver resolver) {
        this.images = images;
        this.resolver = resolver;
    }

    @PostConstruct
    void seed() {
        try {
            Path target = Path.of(images.getLocalDir()).toAbsolutePath().normalize();
            Resource[] resources = resolver.getResources(SEED_PATTERN);
            int copied = 0;
            int skipped = 0;
            for (Resource resource : resources) {
                try {
                    String relative = relativeToSeedRoot(resource);
                    if (relative == null) {
                        skipped++;
                        continue;
                    }
                    Path dest = target.resolve(relative).normalize();
                    // Defense-in-depth against a crafted seed path escaping the target dir.
                    if (!dest.startsWith(target)) {
                        skipped++;
                        continue;
                    }
                    if (Files.exists(dest)) {
                        skipped++;
                        continue;
                    }
                    Files.createDirectories(dest.getParent());
                    try (InputStream in = resource.getInputStream()) {
                        Files.copy(in, dest);
                    }
                    copied++;
                } catch (Exception perFile) {
                    log.warn("Seed image copy skipped for {} -- {}", resource, perFile.getMessage());
                    skipped++;
                }
            }
            log.info("Seed images: target={}, copied={}, skipped={}", target, copied, skipped);
        } catch (Exception fatal) {
            // Never propagate -- logging is the contract. App must boot regardless.
            log.error("Seed images bootstrap failed; application continues without seeding", fatal);
        }
    }

    // Trim everything before (and including) "seed-images/" so we keep the
    // subdir + filename portion (e.g. "categories/soccer.svg").
    private String relativeToSeedRoot(Resource resource) throws IOException {
        String uri = resource.getURI().toString();
        int idx = uri.indexOf(SEED_ROOT_MARKER);
        if (idx < 0) return null;
        String rel = uri.substring(idx + SEED_ROOT_MARKER.length());
        return rel.isEmpty() || rel.endsWith("/") ? null : rel;
    }
}
