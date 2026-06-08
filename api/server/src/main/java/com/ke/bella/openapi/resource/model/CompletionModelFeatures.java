package com.ke.bella.openapi.resource.model;

import com.ke.bella.openapi.protocol.IModelFeatures;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class CompletionModelFeatures implements IModelFeatures {
    private boolean stream;
    private boolean function_call;
    private boolean stream_function_call;
    private boolean parallel_tool_calls;
    private boolean vision;
    private boolean json_format;
    private boolean json_schema;
    private boolean reason_content;
    private boolean reason_content_input;
    private boolean prompt_cache;
    private boolean support_temperature = true;
    private boolean support_top_P = true;
    private boolean support_max_tokens = true;

    @Override
    public Map<String, String> description() {
        Map<String, String> desc = new LinkedHashMap<>();
        desc.put("stream", "Support streaming");
        desc.put("function_call", "Support function call");
        desc.put("stream_function_call", "Support streaming function call");
        desc.put("parallel_tool_calls", "Support parallel tool calls");
        desc.put("vision", "Support vision");
        desc.put("json_format", "Support JSON format output");
        desc.put("json_schema", "Support JSON Schema output");
        desc.put("reason_content", "Support reasoning content output");
        desc.put("reason_content_input", "Support reasoning content input");
        desc.put("prompt_cache", "Support prompt caching");
        desc.put("support_temperature", "Support temperature parameter");
        desc.put("support_top_P", "Support top_p parameter");
        desc.put("support_max_tokens", "Support max_tokens parameter");
        return desc;
    }
}
