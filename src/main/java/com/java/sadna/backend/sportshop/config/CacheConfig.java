package com.java.sadna.backend.sportshop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Categories are a tiny, rarely-written catalog hit on every storefront/admin
// list — cache them so we skip the DB findAll on repeat reads. In-process
// Caffeine is fine for a single app instance; multi-instance shared freshness
// needs a Redis (or similar) CacheManager so eviction/TTL are visible on every node.
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CATEGORIES = "categories";

    @Bean
    public CacheManager cacheManager(CacheProperties cacheProperties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CATEGORIES);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheProperties.getCategoriesTtl()));
        return cacheManager;
    }
}
