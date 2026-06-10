package com.ke.bella.openapi.domain.protocol.asr;

import com.ke.bella.openapi.domain.protocol.AuthorizationProperty;
import lombok.Data;

@Data
public class AsrProperty {
    String deployName;
    AuthorizationProperty auth;
}
