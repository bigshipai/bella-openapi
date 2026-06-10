package com.ke.bella.openapi.domain.protocol.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VertexProperty extends CompletionProperty {
    boolean supportSystemInstruction = true;
    boolean supportThinkConfig = false;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = super.description();
        map.put("supportSystemInstruction", "Support system instruction");
        map.put("supportThinkConfig", "Support thinking config");
        return map;
    }
}
