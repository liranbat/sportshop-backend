package com.java.sadna.backend.sportshop.config.async;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.async.cleanup")
@Validated
public class CleanupExecutorProperties {

    private final int poolSize;
    private final int queueCapacity;
    private final Duration awaitTermination;

    public CleanupExecutorProperties(
            @DefaultValue("6") @Min(1) int poolSize,
            @DefaultValue("100") @Min(0) int queueCapacity,
            @DefaultValue("30s") Duration awaitTermination) {
        this.poolSize = poolSize;
        this.queueCapacity = queueCapacity;
        this.awaitTermination = awaitTermination;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public Duration getAwaitTermination() {
        return awaitTermination;
    }
}
