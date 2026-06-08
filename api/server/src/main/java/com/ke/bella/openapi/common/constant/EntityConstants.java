package com.ke.bella.openapi.common.constant;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicCategory.AUDIO2TEXT;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicCategory.CHAT;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicCategory.IMAGES;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicCategory.TEXT2SPEECH;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicEndpoint.COMPLETION_ENDPOINT;

public class EntityConstants {
    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    public static final String PROTECTED = "protected";
    public static final String INNER = "inner";
    public static final String MAINLAND = "mainland";
    public static final String OVERSEAS = "overseas";
    public static final String BASIC_ROLE = "low";
    public static final String MANAGER_ROLE = "console";
    public static final List<String> DATA_DESTINATIONS = ImmutableList.of(PROTECTED, INNER, MAINLAND, OVERSEAS);
    public static final String HIGH = "high";
    public static final String NORMAL = "normal";
    public static final String LOW = "low";
    public static final List<String> CHANNEL_PRIORITY = ImmutableList.of(HIGH, NORMAL, LOW);
    public static final String MODEL = "model";
    public static final String ENDPOINT = "endpoint";
    public static final List<String> ENTITY_TYPES = ImmutableList.of(MODEL, ENDPOINT);
    public static final String SYSTEM = "system";
    public static final String ORG = "org";
    public static final String PERSON = "person";
    public static final String PROJECT = "project";
    public static final String CONSOLE = "console";
    public static final String ALL = "all";
    public static final List<String> OWNER_TYPES = ImmutableList.of(SYSTEM, ORG, PERSON, PROJECT);
    public static final List<String> AUTHORIZER_TYPES = ImmutableList.of(ORG, PERSON, PROJECT);

    public static final Byte LOWEST_SAFETY_LEVEL = 10;
    public static final Byte HIGHEST_SAFETY_LEVEL = 40;

    @AllArgsConstructor
    @Getter
    public enum SystemBasicEndpoint {
        COMPLETION_ENDPOINT("/v*/chat/completions", "Chat", CHAT),
        MESSAGES_ENDPOINT("/v*/messages", "Chat", CHAT),
        EMBEDDING_ENDPOINT("/v*/embeddings", "Embeddings", CHAT),
        SPEECH_ENDPOINT("/v*/audio/speech", "TTS", TEXT2SPEECH),
        ASR_ENDPOINT("/v*/audio/transcriptions", "ASR", AUDIO2TEXT),
        TEXT2IMAGE_ENDPOINT("/v*/images/generations", "Text-to-Image", IMAGES),
        IMAGE2IMAGE_ENDPOINT("/v*/images/edits", "Image-to-Image", IMAGES);

        private final String endpoint;
        private final String name;
        private final SystemBasicCategory category;
    }

    @AllArgsConstructor
    @Getter
    public enum SystemBasicCategory {
        CHAT("0001", "Language", null),
        AUDIO("0002", "Audio", null),
        IMAGES("0003", "Images", null),
        TEXT2SPEECH("0002-0001", "TTS", AUDIO),
        AUDIO2TEXT("0002-0002", "ASR", AUDIO),
        ;

        private final String code;
        private final String name;
        private final SystemBasicCategory parent;
    }

    @AllArgsConstructor
    @Getter
    public enum ModelJsonKey {
        MAX_INPUT(COMPLETION_ENDPOINT.endpoint, "properties", "max_input_context", Integer.class, "Max Input"),
        MAX_OUTPUT(COMPLETION_ENDPOINT.endpoint, "properties", "max_output_context", Integer.class, "Max Output"),
        FUNCTION_CALL(COMPLETION_ENDPOINT.endpoint, "features", "function_call", Boolean.class, "Support function call"),
        STEAM(COMPLETION_ENDPOINT.endpoint, "features", "stream", Boolean.class, "Support streaming"),
        STREAM_FUNCTION_CALL(COMPLETION_ENDPOINT.endpoint, "features", "stream_function_call", Boolean.class, "Support streaming function call"),
        PARALLEL_TOOL_CALLS(COMPLETION_ENDPOINT.endpoint, "features", "parallel_tool_calls", Boolean.class, "Support parallel tool calls"),
        VISION(COMPLETION_ENDPOINT.endpoint, "features", "vision", Boolean.class, "Support image input"),
        JSON_FORMAT(COMPLETION_ENDPOINT.endpoint, "features", "json_format", Boolean.class, "Support JSON mode"),
        ;

        private final String endpoint;
        private final String fied;
        private final String code;
        private final Class<?> type;
        private final String description;
    }

    @AllArgsConstructor
    @Getter
    public enum Protocol {
        OPENAPI("OpenAIAdaptor"),
        ALI("AliAdaptor"),
        AWS("AwsAdaptor"),
        HUOSHAN("HuoShanAdaptor"),
        ;

        private final String code;
    }

    @AllArgsConstructor
    @Getter
    public enum Supplier {
        Ke("ke", "Ke"),
        ALI("ali", "Alibaba Bailian"),
        HUO_SHAN("Huoshan", "Huoshan"),
        AWS("Bedrock", "Bedrock"),
        AZURE("Azure", "Azure");

        private final String code;
        private final String name;
    }

}
