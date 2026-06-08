package com.ke.bella.openapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async task config
 *
 * @author claude
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncCacheTaskExecutorConfig {

    /**
     * Cache cleanup task thread pool
     */
    @Bean("cacheTaskExecutor")
    public Executor cacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("cache-clear-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Set task decorator, add exception handling
        executor.setTaskDecorator(runnable -> () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.warn("Async cache cleanup task failed", e);
            }
        });

        executor.initialize();
        return executor;
    }
}
