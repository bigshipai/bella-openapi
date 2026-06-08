package com.ke.bella.openapi.protocol.log;

import com.ke.bella.openapi.common.context.EndpointProcessData;

public interface EndpointLogHandler {
    void process(EndpointProcessData endpointProcessData);

    String endpoint();
}
