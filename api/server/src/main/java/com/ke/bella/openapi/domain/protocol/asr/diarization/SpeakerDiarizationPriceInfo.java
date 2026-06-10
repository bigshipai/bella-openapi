package com.ke.bella.openapi.domain.protocol.asr.diarization;

import com.google.common.collect.ImmutableMap;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Price information for speaker diarization requests
 * 基于音频时长计费（秒），说话人识别按音频实际时长收费
 */
@Data
public class SpeakerDiarizationPriceInfo implements IPriceInfo, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 说话人识别单价（元/小时）
     */
    private BigDecimal price;
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "CNY/hour";
    }

    @Override
    public Map<String, String> description() {
        return ImmutableMap.of("price", "Speaker recognition price per hour (CNY)");
    }
}
