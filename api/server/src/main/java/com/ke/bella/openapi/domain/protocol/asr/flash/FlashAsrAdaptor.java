package com.ke.bella.openapi.domain.protocol.asr.flash;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.asr.AsrProperty;
import com.ke.bella.openapi.domain.protocol.asr.AsrRequest;

public interface FlashAsrAdaptor<T extends AsrProperty> extends IProtocolAdaptor {
    FlashAsrResponse asr(AsrRequest request, String url, T property, EndpointProcessData processData);

    @Override
    default String endpoint() {
        return "/v1/audio/asr/flash";
    }
}
