package com.ke.bella.openapi.domain.protocol.log;

import com.ke.bella.openapi.common.context.EndpointProcessData;

public interface EndpointLogHandler {
    void process(EndpointProcessData endpointProcessData);

    String endpoint();
}
