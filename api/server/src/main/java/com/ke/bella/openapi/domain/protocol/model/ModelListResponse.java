package com.ke.bella.openapi.domain.protocol.model;

import com.ke.bella.openapi.domain.protocol.ApiListResponse;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ModelListResponse extends ApiListResponse<ModelInfo> {

    public ModelListResponse(List<ModelInfo> data) {
        super(data, "list", null, false);
    }
}
