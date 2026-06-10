package com.ke.bella.openapi.domain.protocol.message;

import com.ke.bella.openapi.domain.protocol.Callbacks;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.completion.CompletionProperty;

public interface MessageAdaptor<T extends CompletionProperty> extends IProtocolAdaptor {

    MessageResponse createMessages(MessageRequest request, String url, T property);

    void streamMessages(MessageRequest request, String url, T property, Callbacks.StreamCompletionCallback callback);

    @Override
    default String endpoint() {
        return "/v1/messages";
    }
}
