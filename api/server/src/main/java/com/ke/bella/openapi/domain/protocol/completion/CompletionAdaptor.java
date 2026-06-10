package com.ke.bella.openapi.domain.protocol.completion;

import com.ke.bella.openapi.domain.protocol.Callbacks;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;

public interface CompletionAdaptor<T extends CompletionProperty> extends IProtocolAdaptor {

    CompletionResponse completion(CompletionRequest request, String url, T property);

    void streamCompletion(CompletionRequest request, String url, T property, Callbacks.StreamCompletionCallback callback);

    @Override
    default String endpoint() {
        return "/v1/chat/completions";
    }
}
