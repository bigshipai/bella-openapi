package com.ke.bella.openapi.domain.protocol.web;

import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web Extract Price Information
 * Pricing details for Tavily web extract services
 * Basic Extract: 1 API credit per 5 successful URL extractions
 * Advanced Extract: 2 API credits per 5 successful URL extractions
 */
@Data
public class WebExtractPriceInfo implements IPriceInfo, Serializable {

    /**
     * Price per basic extraction (分/次提取)
     * Basic Extract: Every successful URL extractions cost 1 API credit
     */
    private BigDecimal basicExtractionPrice;

    /**
     * Price per advanced extraction (分/次提取)
     * Advanced Extract: Every successful URL extractions cost 2 API credits
     */
    private BigDecimal advancedExtractionPrice;

    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "cents/request";
    }

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("basicExtractionPrice", "Basic extraction price (cents/extraction)");
        map.put("advancedExtractionPrice", "Advanced extraction price (cents/extraction)");
        return map;
    }

    @Override
    public String toString() {
        return "Basic extraction: " + basicExtractionPrice + " cents/5 extractions\n" +
                "Advanced extraction: " + advancedExtractionPrice + " cents/5 extractions";
    }
}
