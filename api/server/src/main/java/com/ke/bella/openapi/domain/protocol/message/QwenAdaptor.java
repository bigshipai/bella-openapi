package com.ke.bella.openapi.domain.protocol.message;

import com.ke.bella.openapi.domain.protocol.completion.CompletionAdaptor;
import com.ke.bella.openapi.domain.protocol.completion.OpenAIProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("QwenMessage")
public class QwenAdaptor implements MessageDelegatorAdaptor<OpenAIProperty> {
    @Autowired
    private com.ke.bella.openapi.domain.protocol.completion.QwenAdaptor delegator;

    @Autowired
    private AnthropicAdaptor anthropicAdaptor;

    @Override
    public CompletionAdaptor<OpenAIProperty> delegator() {
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
        return "Qwen Extended OpenAI Protocol Adapter for /v1/messages endpoint";
    }
}
