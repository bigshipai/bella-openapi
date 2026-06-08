package com.ke.bella.openapi.protocol.web;

import com.ke.bella.openapi.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web Search Price Information Pricing details for Tavily web search services
 * Basic Search: 1 API credit per request Advanced Search: 2 API credits
 * per request
 */
@Data
public class WebSearchPriceInfo implements IPriceInfo, Serializable {

    /**
     * Price per basic search request (分/请求) Basic Search: Each request costs 1
     * API credit
     */
    private BigDecimal basicSearchPrice;

    /**
     * Price per advanced search request (分/请求) Advanced Search: Each request
     * costs 2 API credits
     */
    private BigDecimal advancedSearchPrice;

    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "cents/request";
    }

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("basicSearchPrice", "Basic search price (cents/request)");
        map.put("advancedSearchPrice", "Advanced search price (cents/request)");
        return map;
    }

    @Override
    public String toString() {
        return "Basic search: " + basicSearchPrice + " cents/request\n" +
                "Advanced search: " + advancedSearchPrice + " cents/request";
    }
}
