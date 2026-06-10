package com.ke.bella.openapi.domain.protocol.asr.transcription;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.asr.AsrProperty;

public interface TranscriptionsAsrAdaptor<T extends AsrProperty> extends IProtocolAdaptor {

    @Override
    default String endpoint() {
        return "/v1/audio/transcriptions";
    }
}
