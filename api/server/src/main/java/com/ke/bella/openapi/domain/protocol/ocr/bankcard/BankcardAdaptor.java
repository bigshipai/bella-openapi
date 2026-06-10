package com.ke.bella.openapi.domain.protocol.ocr.bankcard;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

public interface BankcardAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrBankcardResponse bankcard(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/bankcard";
    }
}
