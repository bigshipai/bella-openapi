package com.ke.bella.openapi.domain.protocol.ocr.overseaspassport;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

public interface OverseasPassportAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrOverseasPassportResponse overseasPassport(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/overseas-passport";
    }
}
