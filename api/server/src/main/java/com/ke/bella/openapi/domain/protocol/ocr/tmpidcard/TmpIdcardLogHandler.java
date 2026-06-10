package com.ke.bella.openapi.domain.protocol.ocr.tmpidcard;

import com.ke.bella.openapi.domain.protocol.ocr.OcrLogHandler;
import org.springframework.stereotype.Component;

@Component
public class TmpIdcardLogHandler extends OcrLogHandler {
    @Override
    public String endpoint() {
        return "/v1/ocr/tmp-idcard";
    }
}
