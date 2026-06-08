package com.ke.bella.openapi.protocol.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.protocol.AuthorizationProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AwsProperty extends CompletionProperty {
    String region;
    boolean supportThink;
    boolean supportCache;
    Map<String, Object> additionalParams = new HashMap<>();

    @Override
    public Map<String, String> description() {
        Map<String, String> map = super.description();
        map.put("region", "Deployment region");
        map.put("supportThink", "Support thinking process");
        map.put("supportCache", "Support caching");
        map.put("additionalParams", "Additional request parameters");
        return map;
    }
}
