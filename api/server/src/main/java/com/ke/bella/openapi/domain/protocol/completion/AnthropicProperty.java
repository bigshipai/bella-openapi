package com.ke.bella.openapi.domain.protocol.completion;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnthropicProperty extends CompletionProperty {

    @Override
    public String getAnthropicVersion() {
        if(anthropicVersion == null) {
            return "2023-06-01";
        }
        return anthropicVersion;
    }
}
