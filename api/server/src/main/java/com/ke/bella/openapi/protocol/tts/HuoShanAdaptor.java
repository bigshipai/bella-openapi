package com.ke.bella.openapi.protocol.tts;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.ke.bella.openapi.common.exception.OneTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.protocol.BellaWebSocketListener;
import com.ke.bella.openapi.protocol.Callbacks;
import com.ke.bella.openapi.protocol.log.EndpointLogger;
import com.ke.bella.openapi.utils.HttpUtils;
import com.ke.bella.openapi.utils.JacksonUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
@Component("HuoShanTts")
public class HuoShanAdaptor implements TtsAdaptor<HuoShanProperty> {
    @Autowired
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    @Override
    public byte[] tts(TtsRequest request, String url, HuoShanProperty property) {
        HuoShanRequest huoShanRequest = convertTtsRequestToHuoShanRequest(request, property);
        Request.Builder builder = authorizationRequestBuilder(property.getAuth())
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toByte(huoShanRequest)));
        Request httpRequest = builder.build();
        clearLargeData(request, huoShanRequest);
        return processHuoShanResponse(httpRequest);
    }

    @Override
    public void streamTts(TtsRequest request, String url, HuoShanProperty property, Callbacks.StreamCallback callback) {
        Request webSocketRequest = new Request.Builder()
                .url(property.websocketUrl)
                .header("X-Api-App-Key", property.appId)
                .header("X-Api-Access-Key", property.auth.getSecret())
                .header("X-Api-Resource-Id", property.resourceId)
                .header("X-Api-Connect-Id", UUID.randomUUID().toString())
                .build();
        HttpUtils.websocketRequest(webSocketRequest, new BellaWebSocketListener((Callbacks.WebSocketCallback) callback));
    }

    @Override
    public Callbacks.StreamCallback buildCallback(TtsRequest request, Callbacks.Sender byteSender,
            EndpointProcessData processData, EndpointLogger logger) {
        return new HuoshanStreamTtsCallback(request, byteSender, EndpointContext.getProcessData(), logger);
    }

    private HuoShanRequest convertTtsRequestToHuoShanRequest(TtsRequest ttsRequest, HuoShanProperty property) {
        HuoShanRequest.App app = HuoShanRequest.App.builder()
                .appId(property.getAppId())
                .token(property.getAuth().getApiKey().substring(property.getAuth().getApiKey().indexOf(";") + 1))
                .cluster(property.getCluster())
                .build();

        HuoShanRequest.User user = HuoShanRequest.User.builder()
                .uid(ttsRequest.getUser() != null ? ttsRequest.getUser() : "uid")
                .build();

        HuoShanRequest.Audio audio = buildAudioFromRequest(ttsRequest);
        HuoShanRequest.TextRequest request = buildTextRequestFromRequest(ttsRequest);

        return HuoShanRequest.builder()
                .app(app)
                .user(user)
                .audio(audio)
                .request(request)
                .build();
    }

    private HuoShanRequest.Audio buildAudioFromRequest(TtsRequest ttsRequest) {
        if (ttsRequest.getExtra_body() != null && ttsRequest.getExtra_body().containsKey("audio")) {
            Object audioObj = ttsRequest.getExtra_body().get("audio");
            if (audioObj instanceof Map) {
                HuoShanRequest.Audio audio = JacksonUtils.convertValue((Map<String, Object>) audioObj, HuoShanRequest.Audio.class);
                if (ttsRequest.getVoice() != null) {
                    audio.setVoiceType(ttsRequest.getVoice());
                }
                if (ttsRequest.getResponseFormat() != null) {
                    audio.setEncoding(ttsRequest.getResponseFormat());
                }
                if (ttsRequest.getSpeed() != null) {
                    audio.setSpeedRatio(ttsRequest.getSpeed());
                }
                return audio;
            }
        }

        return HuoShanRequest.Audio.builder()
                .voiceType(ttsRequest.getVoice() != null ? ttsRequest.getVoice() : "BV001_streaming")
                .encoding(ttsRequest.getResponseFormat() != null ? ttsRequest.getResponseFormat() : "wav")
                .speedRatio(ttsRequest.getSpeed() != null ? ttsRequest.getSpeed() : 1.0)
                .build();
    }

    private HuoShanRequest.TextRequest buildTextRequestFromRequest(TtsRequest ttsRequest) {
        if (ttsRequest.getExtra_body() != null && ttsRequest.getExtra_body().containsKey("request")) {
            Object requestObj = ttsRequest.getExtra_body().get("request");
            if (requestObj instanceof Map) {
                HuoShanRequest.TextRequest request = JacksonUtils.convertValue((Map<String, Object>) requestObj, HuoShanRequest.TextRequest.class);
                if (ttsRequest.getInput() != null) {
                    request.setText(ttsRequest.getInput());
                }
                if (request.getReqId() == null) {
                    request.setReqId(String.valueOf(UUID.randomUUID()));
                }
                return request;
            }
        }

        return HuoShanRequest.TextRequest.builder()
                .reqId(String.valueOf(UUID.randomUUID()))
                .text(ttsRequest.getInput())
                .textType("")
                .operation("query")
                .build();
    }

    private byte[] processHuoShanResponse(Request httpRequest) {
        HuoShanResponse huoshanResponse = HttpUtils.httpRequest(httpRequest, HuoShanResponse.class);
        if(huoshanResponse == null || huoshanResponse.getCode() != HuoShanResponseCodeEnum.OK.code || huoshanResponse.getData() == null) {
            HttpStatus status = getHttpStatus(HuoShanAdaptor.HuoShanResponseCodeEnum
                    .getByCode(huoshanResponse == null ? HuoShanResponseCodeEnum.OTHER_ERROR.code : huoshanResponse.getCode()));
            throw new OneTokenException.ChannelException(status.value(), status.getReasonPhrase(),
                    huoshanResponse == null ? HuoShanResponseCodeEnum.OTHER_ERROR.message : huoshanResponse.getMessage());
        }
        return BASE64_DECODER.decode(huoshanResponse.getData());
    }

    private HttpStatus getHttpStatus(HuoShanResponseCodeEnum responseCode) {
        switch (responseCode) {
        case INVALID_REQUEST:
            return HttpStatus.BAD_REQUEST;
        case CONCURRENT_LIMIT_EXCEEDED:
            return HttpStatus.TOO_MANY_REQUESTS;
        case BACKEND_SERVICE_BUSY:
            return HttpStatus.SERVICE_UNAVAILABLE;
        case SERVICE_INTERRUPTED:
            return HttpStatus.SERVICE_UNAVAILABLE;
        case TEXT_LENGTH_LIMIT_EXCEEDED:
            return HttpStatus.PAYLOAD_TOO_LARGE;
        case INVALID_TEXT:
            return HttpStatus.BAD_REQUEST;
        case PROCESSING_TIMEOUT:
            return HttpStatus.SERVICE_UNAVAILABLE;
        case PROCESSING_ERROR:
            return HttpStatus.INTERNAL_SERVER_ERROR;
        case AUDIO_ACQUISITION_TIMEOUT:
            return HttpStatus.GATEWAY_TIMEOUT;
        case BACKEND_LINK_ERROR:
            return HttpStatus.BAD_GATEWAY;
        case VOICE_STYLE_NOT_EXIST:
            return HttpStatus.NOT_FOUND;
        default:
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public String getDescription() {
        return "Huoshan Protocol";
    }

    @Override
    public Class<?> getPropertyClass() {
        return HuoShanProperty.class;
    }

    public enum HuoShanResponseCodeEnum {
        OK(3000, "Request OK"),
        INVALID_REQUEST(3001, "Invalid request"),
        CONCURRENT_LIMIT_EXCEEDED(3003, "Concurrent limit exceeded"),
        BACKEND_SERVICE_BUSY(3005, "Backend service busy"),
        SERVICE_INTERRUPTED(3006, "Service interrupted"),
        TEXT_LENGTH_LIMIT_EXCEEDED(3010, "Text length exceeded"),
        INVALID_TEXT(3011, "Invalid text"),
        PROCESSING_TIMEOUT(3030, "Processing timeout"),
        PROCESSING_ERROR(3031, "Processing error"),
        AUDIO_ACQUISITION_TIMEOUT(3032, "Audio acquisition timeout"),
        BACKEND_LINK_ERROR(3040, "Backend link connection error"),
        VOICE_STYLE_NOT_EXIST(3050, "Voice style does not exist"),
        OTHER_ERROR(3060, "Unknown error");

        public final Integer code;
        public final String message;

        HuoShanResponseCodeEnum(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public static HuoShanResponseCodeEnum getByCode(Integer code) {
            for (HuoShanResponseCodeEnum value : HuoShanResponseCodeEnum.values()) {
                if(value.code.equals(code)) {
                    return value;
                }
            }
            return OTHER_ERROR;
        }
    }
}
