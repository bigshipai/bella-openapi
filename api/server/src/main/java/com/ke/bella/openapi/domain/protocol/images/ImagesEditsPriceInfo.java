package com.ke.bella.openapi.domain.protocol.images;

import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ImagesEditsPriceInfo implements IPriceInfo, Serializable {
    private BigDecimal pricePerEdit;
    private BigDecimal imageTokenPrice;
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "CNY/image";
    }

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("pricePerEdit", "Price per image");
        map.put("imageTokenPrice", "Image token price (/1k tokens)");
        return map;
    }

    @Override
    public String toString() {
        return "Price per image: " + pricePerEdit;
    }
}
