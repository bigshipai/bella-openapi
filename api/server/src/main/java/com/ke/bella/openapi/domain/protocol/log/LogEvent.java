package com.ke.bella.openapi.domain.protocol.log;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import lombok.Data;

@Data
public class LogEvent {
    private EndpointProcessData data;
    private String repositoryCode;
    private boolean costOnly;
}
