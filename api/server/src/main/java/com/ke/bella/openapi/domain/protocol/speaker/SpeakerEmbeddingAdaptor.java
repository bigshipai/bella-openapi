package com.ke.bella.openapi.domain.protocol.speaker;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;

public interface SpeakerEmbeddingAdaptor extends IProtocolAdaptor {

    SpeakerEmbeddingResponse speakerEmbedding(SpeakerEmbeddingRequest request, String url, SpeakerEmbeddingProperty property);

    @Override
    default String endpoint() {
        return "/v1/audio/speaker/embedding";
    }
}
