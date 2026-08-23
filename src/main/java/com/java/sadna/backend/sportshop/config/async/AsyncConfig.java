package com.java.sadna.backend.sportshop.config.async;

import com.java.sadna.backend.sportshop.common.constants.AsyncConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    private final CleanupExecutorProperties cleanupProperties;
    private final SalesExecutorProperties salesProperties;

    public AsyncConfig(CleanupExecutorProperties cleanupProperties,
                       SalesExecutorProperties salesProperties) {
        this.cleanupProperties = cleanupProperties;
        this.salesProperties = salesProperties;
    }

    @Bean(AsyncConstants.Executors.CLEANUP)
    public ThreadPoolTaskExecutor cleanupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cleanupProperties.getPoolSize());
        executor.setMaxPoolSize(cleanupProperties.getPoolSize());
        executor.setQueueCapacity(cleanupProperties.getQueueCapacity());
        executor.setThreadNamePrefix("cleanup-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // keeps the request's trace id, which lives in a thread-local
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds((int) cleanupProperties.getAwaitTermination().toSeconds());
        return executor;
    }

    @Bean(AsyncConstants.Executors.SALES_FANOUT)
    public ThreadPoolTaskExecutor salesFanoutExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(salesProperties.getPoolSize());
        executor.setMaxPoolSize(salesProperties.getPoolSize());
        executor.setQueueCapacity(salesProperties.getQueueCapacity());
        executor.setThreadNamePrefix("sales-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // keeps the request's trace id, which lives in a thread-local
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds((int) salesProperties.getAwaitTermination().toSeconds());
        return executor;
    }
}
