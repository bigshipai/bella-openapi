package com.ke.bella.openapi.domain.protocol.images;

import com.ke.bella.openapi.domain.protocol.IModelFeatures;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ImagesModelFeatures implements IModelFeatures {
    private boolean highQuality = false;
    private boolean multipleStyles = false;
    private boolean customSize = false;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("highQuality", "Support high quality generation");
        map.put("multipleStyles", "Support multiple styles");
        map.put("customSize", "Support custom size");
        return map;
    }
}
