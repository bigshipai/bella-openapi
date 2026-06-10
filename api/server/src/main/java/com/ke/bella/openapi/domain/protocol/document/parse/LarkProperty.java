package com.ke.bella.openapi.domain.protocol.document.parse;

import lombok.Data;

import java.util.Map;

@Data
public class LarkProperty extends DocParseProperty {
    private String clientId;
    private String clientSecret;
    private String uploadDirToken;
    private String cloudDirToken;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = super.description();
        map.put("clientId", "clientId");
        map.put("clientSecret", "clientSecret");
        map.put("uploadDirToken", "Upload document directory token");
        map.put("cloudDirToken", "Cloud document directory token");
        return map;
    }
}
