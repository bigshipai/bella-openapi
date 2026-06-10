package com.ke.bella.openapi.controller.endpoint.dto;

import com.ke.bella.openapi.domain.protocol.UserRequest;
import com.ke.bella.openapi.domain.protocol.document.parse.SourceFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocParseRequest implements UserRequest {
    @NotNull
    private SourceFile file;
    private String user;
    private String model;
    private String type; // task, blocking
    private String callbackUrl;
    private int maxTimeoutMillis;
}
