package com.ke.bella.openapi.protocol.tts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ke.bella.openapi.protocol.IModelFeatures;
import lombok.Data;

@Data
public class TTSModelFeatures implements IModelFeatures {

    private boolean stream;
    private boolean customize_sound_color;

    @Override
    public Map<String, String> description() {
        Map<String, String> desc = new LinkedHashMap<>();
        desc.put("stream", "Support streaming output");
        desc.put("customize_sound_color", "Support custom voice");
        return desc;
    }
}
