package com.ke.bella.openapi.domain.protocol.message;

import com.ke.bella.openapi.domain.protocol.completion.CompletionAdaptor;
import com.ke.bella.openapi.domain.protocol.completion.ResponsesApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ResponsesApiMessage")
public class ResponsesApiAdaptor implements MessageDelegatorAdaptor<ResponsesApiProperty> {
    @Autowired
    private com.ke.bella.openapi.domain.protocol.completion.ResponsesApiAdaptor delegator;

    @Autowired
    private AnthropicAdaptor anthropicAdaptor;

    @Override
    public CompletionAdaptor<ResponsesApiProperty> delegator() {
        return delegator;
    }

    @Override
    public AnthropicAdaptor anthropicAdaptor() {
        return anthropicAdaptor;
    }

    @Override
    public boolean isNativeSupport() {
        return false;
    }

    @Override
    public String getDescription() {
        return "OpenAI Responses API Protocol Adapter for /v1/messages endpoint";
    }
}
