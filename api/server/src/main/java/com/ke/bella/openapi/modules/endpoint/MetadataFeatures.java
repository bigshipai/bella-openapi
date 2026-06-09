package com.ke.bella.openapi.modules.endpoint;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.ke.bella.openapi.common.dto.EnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum MetadataFeatures {
    OVERSEAS("overseas", "Overseas", ImmutableSet.of("*")),
    MAINLAND("mainland", "Mainland", ImmutableSet.of("*")),
    INNER("inner", "Internal", ImmutableSet.of("*")),
    PROTECTED("protected", "Internal (Registered)", ImmutableSet.of("*")),
    LARGE_INPUT_CONTEXT("long_input_context", "Long Context", ImmutableSet.of("/v1/chat/completions")),
    LARGE_OUTPUT_CONTEXT("long_output_context", "Long Output", ImmutableSet.of("/v1/chat/completions")),
    REASON_CONTENT("reason_content", "Deep Reasoning", ImmutableSet.of("/v1/chat/completions")),
    STREAM("stream", "Streaming", ImmutableSet.of("/v1/chat/completions")),
    FUNCTION_CALL("function_call", "Function Call", ImmutableSet.of("/v1/chat/completions")),
    STREAM_FUNCTION_CALL("stream_function_call", "Streaming Function Call", ImmutableSet.of("/v1/chat/completions")),
    VISION("vision", "Vision", ImmutableSet.of("/v1/chat/completions")),
    JSON_FORMAT("json_format", "JSON Format", ImmutableSet.of("/v1/chat/completions")),
    PROMPT_CACHE("prompt_cache", "Prompt Cache", ImmutableSet.of("/v1/chat/completions")),
    HIGH_QUALITY("highQuality", "High Quality", ImmutableSet.of("/v1/images/generations")),
    MULTIPLE_STYLES("multipleStyles", "Multiple Styles", ImmutableSet.of("/v1/images/generations")),
    CUSTOM_SIZE("customSize", "Custom Size", ImmutableSet.of("/v1/images/generations")),
    ;

    private final String code;
    private final String name;
    private final Set<String> endpoint;

    public static List<EnumDto> listFeatures(String endpoint) {
        return Arrays.stream(MetadataFeatures.values()).filter(t -> t.endpoint.contains(endpoint) || t.endpoint.contains("*"))
                .map(t -> new EnumDto(t.getCode(), t.getName())).collect(Collectors.toList());
    }

    public static boolean validate(List<String> features) {
        Set<String> set = Sets.newHashSet(OVERSEAS.code, MAINLAND.code, INNER.code, PROTECTED.code);
        int i = 0;
        for (String feature : features) {
            if(set.contains(feature)) {
                i++;
            }
        }
        return i <= 1;
    }
}
