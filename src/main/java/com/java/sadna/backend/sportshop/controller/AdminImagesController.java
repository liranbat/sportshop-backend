package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.adminimages.api.AdminImagesApi;
import com.java.sadna.backend.sportshop.api.generated.adminimages.model.ImageUploadResponse;
import com.java.sadna.backend.sportshop.model.StoredImageDto;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import com.java.sadna.backend.sportshop.service.ImageStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminImagesController implements AdminImagesApi {

    private final ImageStorageService imageStorageService;

    public AdminImagesController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadAdminImage(String resourceType,
                                                                MultipartFile file) {
        ResourceImagePolicy policy = ResourceImagePolicy.fromUrlSegment(resourceType);
        StoredImageDto stored = imageStorageService.store(file, policy);
        ImageUploadResponse body = new ImageUploadResponse(stored.getUrl());
        return ResponseEntity.ok(body);
    }
}
