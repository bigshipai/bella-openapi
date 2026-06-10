package com.ke.bella.openapi.domain.protocol.ocr.bankcard;

import org.springframework.stereotype.Component;

import com.ke.bella.openapi.domain.protocol.ocr.OcrLogHandler;

@Component
public class BankcardLogHandler extends OcrLogHandler {
    @Override
    public String endpoint() {
        return "/v1/ocr/bankcard";
    }
}
