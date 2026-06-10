package com.ke.bella.openapi.domain.protocol.asr.transcription;

import com.google.common.collect.ImmutableMap;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class TranscriptionsAsrPriceInfo implements IPriceInfo, Serializable {
    private BigDecimal price;
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "CNY/hour";
    }

    @Override
    public Map<String, String> description() {
        return ImmutableMap.of("price", "Price per hour (CNY)");
    }
}
