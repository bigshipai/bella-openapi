package com.ke.bella.openapi.protocol.asr.diarization;

import com.google.common.collect.ImmutableMap;
import com.ke.bella.openapi.protocol.IModelFeatures;
import lombok.Data;

import java.util.Map;

/**
 * 说话人识别模型特性配置
 */
@Data
public class SpeakerDiarizationModelFeatures implements IModelFeatures {

    /**
     * 支持的最大说话人数量
     */
    private int maxSpeakers = 10;

    /**
     * 支持的最小音频时长（秒）
     */
    private int minDurationSeconds = 1;

    /**
     * 支持的最大音频时长（秒）
     */
    private int maxDurationSeconds = 3600;

    /**
     * 是否支持说话人嵌入向量输出
     */
    private boolean embeddingSupported = true;

    /**
     * 是否支持说话人置信度评分
     */
    private boolean confidenceSupported = true;

    /**
     * 支持的音频格式列表
     */
    private String[] supportedFormats = { "wav", "mp3", "flac", "m4a" };

    @Override
    public Map<String, String> description() {
        return ImmutableMap.<String, String>builder()
                .put("maxSpeakers", "Max supported speakers")
                .put("minDurationSeconds", "Min audio duration (seconds)")
                .put("maxDurationSeconds", "Max audio duration (seconds)")
                .put("embeddingSupported", "Support speaker embedding output")
                .put("confidenceSupported", "Support speaker confidence scoring")
                .put("supportedFormats", "Supported audio format list")
                .build();
    }
}
