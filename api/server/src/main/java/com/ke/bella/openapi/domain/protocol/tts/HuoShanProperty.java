package com.ke.bella.openapi.domain.protocol.tts;

import com.ke.bella.openapi.domain.protocol.AuthorizationProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HuoShanProperty extends TtsProperty {
    AuthorizationProperty auth;
    String deployName;
    String appId;
    String cluster;
    String resourceId;
    String websocketUrl;
}
