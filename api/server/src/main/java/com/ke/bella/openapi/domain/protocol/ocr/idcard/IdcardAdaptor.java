package com.ke.bella.openapi.domain.protocol.ocr.idcard;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

public interface IdcardAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrIdcardResponse idcard(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/idcard";
    }
}
