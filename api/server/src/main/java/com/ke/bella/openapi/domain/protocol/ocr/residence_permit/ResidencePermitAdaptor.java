package com.ke.bella.openapi.domain.protocol.ocr.residence_permit;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;
import com.ke.bella.openapi.domain.protocol.ocr.residencepermit.OcrResidencePermitResponse;

public interface ResidencePermitAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrResidencePermitResponse hmtResidencePermit(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/hmt-residence-permit";
    }
}
