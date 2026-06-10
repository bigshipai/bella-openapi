package com.ke.bella.openapi.domain.protocol.tts;

import com.ke.bella.openapi.domain.protocol.IModelProperties;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class VoiceProperties implements IModelProperties {

    private Map<String, String> voiceTypes;
    private String input_state;

    @Override
    public Map<String, String> description() {
        Map<String, String> desc = new LinkedHashMap<>();
        desc.put("voiceTypes", "Voice types");
        desc.put("input_state", "Additional notes");
        return desc;
    }
}
