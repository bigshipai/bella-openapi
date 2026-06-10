package com.ke.bella.openapi.domain.protocol.tts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HuoShanResponse implements Serializable {
    @JsonProperty("reqid")
    private String reqId;
    private int code;
    private String message;
    private int sequence;
    private String data;
    private Addition addition;
    private String duration;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Addition {
        private String duration;
    }
}
