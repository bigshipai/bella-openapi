package com.ke.bella.openapi.domain.protocol.ocr.hmt_travel_permit;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.ocr.OcrProperty;
import com.ke.bella.openapi.controller.endpoint.dto.OcrRequest;
import com.ke.bella.openapi.domain.protocol.ocr.hmttravelpermit.OcrHmtTravelPermitResponse;

public interface HmtTravelPermitAdaptor<T extends OcrProperty> extends IProtocolAdaptor {

    OcrHmtTravelPermitResponse hmtTravelPermit(OcrRequest request, String url, T property);

    @Override
    default String endpoint() {
        return "/v1/ocr/hmt-travel-permit";
    }
}
