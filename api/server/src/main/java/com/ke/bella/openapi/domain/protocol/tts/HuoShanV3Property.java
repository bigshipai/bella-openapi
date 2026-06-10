package com.ke.bella.openapi.domain.protocol.tts;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HuoShanV3Property extends TtsProperty {
    String appId;
    String accessKey;
    String resourceId;
}
