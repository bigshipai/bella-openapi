package com.ke.bella.openapi.config.web;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneTokenServerContext {
    private String ip;
    private Integer port;
    private String applicationName;
}
