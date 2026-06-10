package com.ke.bella.openapi.domain.protocol.ocr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableSortedMap;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;

import lombok.Data;

/**
 * OCR计费信息模型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OcrPriceInfo implements IPriceInfo, Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal pricePerRequest;     // 每次请求价格
    private String unit = "CNY/request";           // 计费单位
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public Map<String, String> description() {
        return ImmutableSortedMap.of("pricePerRequest", "Price per request");
    }
}
