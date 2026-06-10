package com.ke.bella.openapi.domain.protocol.web;

import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web Crawl Price Information
 * Pricing details for Tavily web crawl services
 * Crawl Cost = Mapping Cost + Extraction Cost
 */
@Data
public class WebCrawlPriceInfo implements IPriceInfo, Serializable {

    /**
     * Price per basic mapping (per 10 pages)
     */
    private BigDecimal basicMappingPrice;

    /**
     * Price per mapping with instructions (per 10 pages)
     */
    private BigDecimal instructionMappingPrice;

    /**
     * Price per basic extraction (per 5 extractions)
     */
    private BigDecimal basicExtractionPrice;

    /**
     * Price per advanced extraction (per 5 extractions)
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
        map.put("basicMappingPrice", "Basic mapping price (cents/page)");
        map.put("instructionMappingPrice", "Instruction mapping price (cents/page)");
        map.put("basicExtractionPrice", "Basic extraction price (cents/extraction)");
        map.put("advancedExtractionPrice", "Advanced extraction price (cents/extraction)");
        return map;
    }

    @Override
    public String toString() {
        return "Basic mapping: " + basicMappingPrice + " cents/10 pages\n" +
                "Instruction mapping: " + instructionMappingPrice + " cents/10 pages\n" +
                "Basic extraction: " + basicExtractionPrice + " cents/5 times\n" +
                "Advanced extraction: " + advancedExtractionPrice + " cents/5 extractions";
    }
}
