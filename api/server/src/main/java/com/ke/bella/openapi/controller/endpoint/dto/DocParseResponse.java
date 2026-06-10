package com.ke.bella.openapi.controller.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ke.bella.openapi.domain.protocol.document.parse.DocParseResult;
import lombok.Data;

@Data
public class DocParseResponse {
    private DocParseResult result;
    private String status; // success, failed, processing
    private String message;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private Runnable callback;
}
