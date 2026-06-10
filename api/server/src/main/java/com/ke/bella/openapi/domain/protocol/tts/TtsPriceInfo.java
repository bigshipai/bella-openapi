package com.ke.bella.openapi.domain.protocol.tts;

import com.google.common.collect.ImmutableMap;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class TtsPriceInfo implements IPriceInfo, Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal input;
    private String unit = "cents/10k chars";
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public Map<String, String> description() {
        return ImmutableMap.of("input", "Input character unit price (cents/10k chars)");
    }
}
