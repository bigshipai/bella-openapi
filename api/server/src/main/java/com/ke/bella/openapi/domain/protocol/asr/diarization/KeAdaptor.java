package com.ke.bella.openapi.domain.protocol.asr.diarization;

import com.ke.bella.openapi.domain.protocol.ApiResponse;
import com.ke.bella.openapi.domain.protocol.asr.AudioTranscriptionRequest.AudioTranscriptionReq;
import com.ke.bella.openapi.utils.HttpUtils;
import com.ke.bella.openapi.utils.JacksonUtils;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

@Component("KeSpeakerDiarization")
public class KeAdaptor implements SpeakerDiarizationAdaptor<SpeakerDiarizationProperty> {

    @Override
    public SpeakerDiarizationResponse speakerDiarization(AudioTranscriptionReq request, String url, SpeakerDiarizationProperty property) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toByte(request)));

        Request httpRequest = builder.build();
        return doRequest(httpRequest);
    }

    protected SpeakerDiarizationResponse doRequest(Request httpRequest) {
        return HttpUtils.httpRequest(httpRequest, SpeakerDiarizationResponse.class, ((response, httpResponse) -> {
            if(httpResponse.code() != 200 && response.getError() == null) {
                response.setError(ApiResponse.OpenapiError.builder()
                        .httpCode(httpResponse.code())
                        .message(httpResponse.message())
                        .type("HTTP_ERROR")
                        .build());
            }
        }));
    }

    @Override
    public String getDescription() {
        return "Ke Private Protocol";
    }

    @Override
    public Class<?> getPropertyClass() {
        return SpeakerDiarizationProperty.class;
    }
}
