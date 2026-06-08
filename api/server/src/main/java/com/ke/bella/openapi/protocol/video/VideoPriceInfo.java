package com.ke.bella.openapi.protocol.video;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.protocol.IPriceInfo;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoPriceInfo implements IPriceInfo, Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal input;

    private BigDecimal output;

    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "cents/1k tokens";
    }

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("input", "Input token unit price (cents/1k tokens)");
        map.put("output", "Output token unit price (cents/1k tokens)");
        return map;
    }
}
