package com.ke.bella.openapi.domain.protocol.speaker;

import com.ke.bella.openapi.domain.protocol.ApiResponse;
import com.ke.bella.openapi.utils.HttpUtils;
import com.ke.bella.openapi.utils.JacksonUtils;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

@Component("KeSpeakerEmbedding")
public class KeAdaptor implements SpeakerEmbeddingAdaptor {

    @Override
    public SpeakerEmbeddingResponse speakerEmbedding(SpeakerEmbeddingRequest request, String url, SpeakerEmbeddingProperty property) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toByte(request)));

        Request httpRequest = builder.build();
        clearLargeData(request);
        return doRequest(httpRequest);
    }

    protected SpeakerEmbeddingResponse doRequest(Request httpRequest) {
        return HttpUtils.httpRequest(httpRequest, SpeakerEmbeddingResponse.class, ((response, httpResponse) -> {
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
        return SpeakerEmbeddingProperty.class;
    }
}
