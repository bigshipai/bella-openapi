package com.ke.bella.openapi.domain.protocol.web;

import com.ke.bella.openapi.controller.endpoint.dto.WebSearchRequest;
import com.ke.bella.openapi.controller.endpoint.dto.WebSearchResponse;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;

/**
 * Web Search Adaptor Interface Adaptor interface for web search functionality
 * using Tavily Search API
 */
public interface WebSearchAdaptor<T extends WebSearchProperty> extends IProtocolAdaptor {

    /**
     * Perform web search
     *
     * @param request  web search request parameters
     * @param url      request URL
     * @param property protocol configuration properties
     *
     * @return web search response
     */
    WebSearchResponse search(WebSearchRequest request, String url, T property);
}
