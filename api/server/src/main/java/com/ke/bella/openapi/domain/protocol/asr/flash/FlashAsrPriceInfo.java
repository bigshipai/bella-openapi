package com.ke.bella.openapi.domain.protocol.asr.flash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

@Data
public class FlashAsrPriceInfo implements IPriceInfo, Serializable {

    private BigDecimal price;
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "cents/request";
    }

    @Override
    public Map<String, String> description() {
        return ImmutableMap.of("price", "cents/request");
    }
}
