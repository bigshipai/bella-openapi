package com.ke.bella.openapi.domain.protocol.tts;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.controller.endpoint.dto.TtsRequest;
import com.ke.bella.openapi.domain.protocol.Callbacks;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.log.EndpointLogger;

public interface TtsAdaptor<T extends TtsProperty> extends IProtocolAdaptor {

    byte[] tts(TtsRequest request, String url, T property);

    void streamTts(TtsRequest request, String url, T property, Callbacks.StreamCallback callback);

    Callbacks.StreamCallback buildCallback(TtsRequest request, Callbacks.Sender byteSender, EndpointProcessData processData, EndpointLogger logger);

    @Override
    default String endpoint() {
        return "/v1/audio/speech";
    }
}
