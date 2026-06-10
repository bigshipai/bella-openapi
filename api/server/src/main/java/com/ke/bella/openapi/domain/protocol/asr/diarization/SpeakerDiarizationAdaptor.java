package com.ke.bella.openapi.domain.protocol.asr.diarization;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.asr.AudioTranscriptionRequest.AudioTranscriptionReq;

public interface SpeakerDiarizationAdaptor<T extends SpeakerDiarizationProperty> extends IProtocolAdaptor {

    @Override
    default String endpoint() {
        return "/v1/audio/speaker/diarization";
    }

    SpeakerDiarizationResponse speakerDiarization(AudioTranscriptionReq request, String url, T property);
}
