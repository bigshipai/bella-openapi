package com.ke.bella.openapi.job.queue.config;

import lombok.Data;

@Data
public class JobQueueProperties {
    private String url;
    private Integer defaultTimeout = 300;
}
