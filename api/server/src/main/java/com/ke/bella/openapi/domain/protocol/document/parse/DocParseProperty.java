package com.ke.bella.openapi.domain.protocol.document.parse;

import com.ke.bella.openapi.domain.protocol.IProtocolProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class DocParseProperty implements IProtocolProperty {
    private String[] supportTypes;

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("supportTypes", "Supported types");
        return map;
    }
}
