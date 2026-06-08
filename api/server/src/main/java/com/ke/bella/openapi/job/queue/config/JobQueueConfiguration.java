package com.ke.bella.openapi.job.queue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobQueueConfiguration {

    @Bean
    @ConfigurationProperties(value = "bella.job-queue")
    public JobQueueProperties jobQueueProperties() {
        return new JobQueueProperties();
    }
}
