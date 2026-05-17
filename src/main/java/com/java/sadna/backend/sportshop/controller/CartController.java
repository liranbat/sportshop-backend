package com.java.sadna.backend.sportshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// QA-only stub: gives the /cart frontend route a real authenticated backend call
// so the 401 -> refresh -> retry interceptor flow can be exercised end-to-end.
// Will be replaced by the real cart API (with its own OpenAPI spec, DTOs, service,
// persistence) when the cart feature ships.
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCart() {
        return ResponseEntity.ok(Map.of("items", List.of()));
    }
}
