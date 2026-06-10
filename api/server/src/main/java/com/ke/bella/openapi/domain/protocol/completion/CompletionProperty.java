package com.ke.bella.openapi.domain.protocol.completion;

import com.ke.bella.openapi.domain.protocol.AuthorizationProperty;
import com.ke.bella.openapi.domain.protocol.IProtocolProperty;
import com.ke.bella.openapi.controller.safety.SafetyCheckMode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class CompletionProperty implements IProtocolProperty {
    String encodingType = StringUtils.EMPTY;
    boolean mergeReasoningContent = false;
    boolean splitReasoningFromContent = false;
    boolean functionCallSimulate = false;
    Map<String, String> extraHeaders;
    String queueName;
    String safetyCheckMode = SafetyCheckMode.async.name();
    String anthropicVersion;
    String messageEndpointUrl;
    Integer defaultMaxToken;
    String deployName;
    AuthorizationProperty auth;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("encodingType", "Encoding type");
        map.put("mergeReasoningContent", "Merge reasoning content");
        map.put("splitReasoningFromContent", "Split reasoning from content");
        map.put("functionCallSimulate", "Force support function call");
        map.put("extraHeaders", "Extra request headers");
        map.put("queueName", "Queue (requests proxied by bella-job-queue service when configured)");
        map.put("anthropicVersion", "Anthropic API version (e.g. 2023-06-01, for native proxy)");
        map.put("messageEndpointUrl", "Message API endpoint URL (enables Anthropic native proxy when configured)");
        map.put("defaultMaxToken", "Default max output tokens");
        map.put("deployName", "Deployment name / Model name");
        map.put("auth", "Auth config");
        return map;
    }
}
