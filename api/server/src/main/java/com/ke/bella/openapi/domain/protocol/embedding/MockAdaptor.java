package com.ke.bella.openapi.domain.protocol.embedding;

import com.ke.bella.openapi.common.exception.BizParamCheckException;
import org.springframework.stereotype.Component;

@Component("mockEmbedding")
public class MockAdaptor implements EmbeddingAdaptor<EmbeddingProperty> {
    @Override
    public String getDescription() {
        return "Mock Protocol";
    }

    @Override
    public Class<?> getPropertyClass() {
        return EmbeddingAdaptor.class;
    }

    @Override
    public EmbeddingResponse embedding(EmbeddingRequest request, String url, EmbeddingProperty property) {
        throw new BizParamCheckException("Embedding mock is not yet supported");
    }
}
