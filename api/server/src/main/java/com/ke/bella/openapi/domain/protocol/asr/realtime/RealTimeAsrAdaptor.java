package com.ke.bella.openapi.domain.protocol.asr.realtime;

import com.ke.bella.openapi.domain.protocol.asr.AsrProperty;
import com.ke.bella.openapi.domain.protocol.realtime.RealTimeAdaptor;

public interface RealTimeAsrAdaptor<T extends AsrProperty> extends RealTimeAdaptor<T> {
    default String endpoint() {
        return "/v1/audio/asr/stream";
    }
}
