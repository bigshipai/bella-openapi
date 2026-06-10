package com.ke.bella.openapi.domain.protocol.ocr.businesslicense;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;

public interface BusinessLicenseAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrBusinessLicenseResponse businessLicense(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/business-license";
    }
}
