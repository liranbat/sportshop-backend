package com.java.sadna.backend.sportshop.config.async;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.async.sales")
@Validated
public class SalesExecutorProperties {

    private final int poolSize;
    private final int queueCapacity;
    private final Duration awaitTermination;
    private final Duration resultsTimeout;

    public SalesExecutorProperties(
            @DefaultValue("18") @Min(1) int poolSize,
            @DefaultValue("18") @Min(0) int queueCapacity,
            @DefaultValue("10s") Duration awaitTermination,
            @DefaultValue("60s") Duration resultsTimeout) {
        this.poolSize = poolSize;
        this.queueCapacity = queueCapacity;
        this.awaitTermination = awaitTermination;
        this.resultsTimeout = resultsTimeout;
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

    public Duration getResultsTimeout() {
        return resultsTimeout;
    }
}
