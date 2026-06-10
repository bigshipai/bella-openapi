package com.ke.bella.openapi.domain.protocol.images;

import com.ke.bella.openapi.domain.protocol.AuthorizationProperty;
import com.ke.bella.openapi.domain.protocol.IProtocolProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ImagesProperty implements IProtocolProperty {
    AuthorizationProperty auth;
    String deployName;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("auth", "Auth config");
        map.put("deployName", "Deployment name");
        return map;
    }
}
