package com.ke.bella.openapi.domain.protocol.ocr.general;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

/**
 * 通用OCR识别适配器接口
 */
public interface GeneralAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrGeneralResponse general(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/general";
    }
}
