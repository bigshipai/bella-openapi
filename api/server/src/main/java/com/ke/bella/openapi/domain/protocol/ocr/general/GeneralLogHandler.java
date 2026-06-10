package com.ke.bella.openapi.domain.protocol.ocr.general;

import org.springframework.stereotype.Component;

import com.ke.bella.openapi.domain.protocol.ocr.OcrLogHandler;

@Component
public class GeneralLogHandler extends OcrLogHandler {
    @Override
    public String endpoint() {
        return "/v1/ocr/general";
    }
}
