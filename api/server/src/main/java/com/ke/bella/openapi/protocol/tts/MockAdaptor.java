package com.ke.bella.openapi.protocol.tts;

import org.springframework.stereotype.Component;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.protocol.Callbacks;
import com.ke.bella.openapi.protocol.log.EndpointLogger;

@Component("mockTts")
public class MockAdaptor implements TtsAdaptor<TtsProperty> {
    @Override
    public String getDescription() {
        return "Mock Protocol";
    }

    @Override
    public Class<?> getPropertyClass() {
        return TtsAdaptor.class;
    }

    @Override
    public byte[] tts(TtsRequest request, String url, TtsProperty property) {
        throw new BizParamCheckException("TTS mock is not yet supported");
    }

    @Override
    public void streamTts(TtsRequest request, String url, TtsProperty property, Callbacks.StreamCallback callback) {
        throw new BizParamCheckException("TTS mock is not yet supported");
    }

    @Override
    public Callbacks.StreamCallback buildCallback(TtsRequest request, Callbacks.Sender byteSender,
            EndpointProcessData processData, EndpointLogger logger) {
        throw new BizParamCheckException("TTS mock is not yet supported");
    }

}
