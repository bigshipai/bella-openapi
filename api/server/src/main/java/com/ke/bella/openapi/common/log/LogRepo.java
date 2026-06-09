package com.ke.bella.openapi.common.log;

import com.ke.bella.openapi.common.context.EndpointProcessData;

public interface LogRepo {
    void record(EndpointProcessData log);
}
