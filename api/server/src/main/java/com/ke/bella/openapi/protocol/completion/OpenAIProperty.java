package com.ke.bella.openapi.protocol.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.protocol.AuthorizationProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIProperty extends CompletionProperty {
    String apiVersion;
    boolean supportStreamOptions = true;
    String[] abandonFields;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = super.description();
        map.put("apiVersion", "API version (fill when URL needs version)");
        map.put("supportStreamOptions", "Support StreamOptions parameter");
        map.put("abandonFields", "Disabled parameters");
        return map;
    }
}
