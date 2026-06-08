package com.ke.bella.openapi.protocol.asr;

import com.ke.bella.openapi.protocol.realtime.RealTimeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TencentRealTimeAsrRequest {
    private String voiceId; 			// Globally unique identifier for audio stream

    private String appid;
    private String secretid;
    private String secretkey;

    private String engineModelType; 	// Engine model type
    private Integer voiceFormat; 		// Audio format code: 1-PCM
    private Integer inputSampleRate; 	// Sample rate

    private Integer wordInfo;    		// Whether to show word-level timestamps
    private String hotwordList; 		// Temporary hotwords list,
                                		// Format: "hotword1|10,hotword2|5,hotword3|11", priority higher than
                                		// hotwordId
    private String hotwordId;    		// Hotwords table ID
    private String replaceTextId; 		// Replacement vocabulary ID, suitable for extreme case phrases that hotwords and self-learning scenarios cannot solve

    public TencentRealTimeAsrRequest(RealTimeMessage request, TencentProperty property) {
        this.voiceId = java.util.UUID.randomUUID().toString();
        this.appid = property.getAppid();
        this.secretid = property.getAuth().getApiKey();
        this.secretkey = property.getAuth().getSecret();
        this.engineModelType = property.getEngineModelType();
        this.voiceFormat = getVoiceFormat(request.getPayload().getFormat());
        this.inputSampleRate = request.getPayload().getSampleRate();

        this.hotwordId = request.getPayload().getHotWordsTableId() != null ? request.getPayload().getHotWordsTableId() : null;
        this.hotwordList = request.getPayload().getHotWords() != null ? request.getPayload().getHotWords() : null;
        this.wordInfo = request.getPayload().getEnableWords() != null ? (request.getPayload().getEnableWords() ? 1 : 0) : null;
        this.replaceTextId = request.getPayload().getVocabularyId() != null ? request.getPayload().getVocabularyId() : null;
    }

    private Integer getVoiceFormat(String format) {
        if("pcm".equalsIgnoreCase(format)) {
            return 1;
        } else if("wav".equalsIgnoreCase(format)) {
            return 2;
        } else if("opus".equalsIgnoreCase(format)) {
            return 3;
        } else if("speex".equalsIgnoreCase(format)) {
            return 4;
        } else if("silk".equalsIgnoreCase(format)) {
            return 5;
        } else if("mp3".equalsIgnoreCase(format)) {
            return 6;
        } else if("m4a".equalsIgnoreCase(format)) {
            return 7;
        } else if("aac".equalsIgnoreCase(format)) {
            return 8;
        }
        return 1; // Default PCM
    }
}
