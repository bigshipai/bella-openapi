package com.ke.bella.openapi.domain.protocol.ocr.tmpidcard;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

public interface TmpIdcardAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrTmpIdcardResponse tmpIdcard(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/tmp-idcard";
    }
}
