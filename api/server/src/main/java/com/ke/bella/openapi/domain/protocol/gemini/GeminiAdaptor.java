package com.ke.bella.openapi.domain.protocol.gemini;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.completion.CompletionProperty;
import com.ke.bella.openapi.domain.protocol.completion.gemini.GeminiRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface GeminiAdaptor<T extends CompletionProperty> extends IProtocolAdaptor {

    void completion(GeminiRequest request, String url, T property, HttpServletResponse response);

    void streamCompletion(GeminiRequest request, String url, T property, HttpServletResponse response);

    @Override
    default String endpoint() {
        return "/v1beta/models";
    }
}
