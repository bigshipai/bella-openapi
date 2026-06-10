package com.ke.bella.openapi.domain.protocol.asr.transcription;

import com.ke.bella.openapi.domain.protocol.asr.AsrProperty;
import org.springframework.stereotype.Component;

@Component("HuoshanTranscriptionsAsr")
public class HuoshanAdaptor implements TranscriptionsAsrAdaptor<AsrProperty> {
    @Override
    public String getDescription() {
        return "Huoshan Protocol";
    }

    @Override
    public Class<?> getPropertyClass() {
        return AsrProperty.class;
    }
}
