package com.ke.bella.openapi.modules.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.common.dto.EnumDto;
import com.ke.bella.openapi.modules.model.Model;
import com.ke.bella.openapi.modules.model.PriceDetails;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointDetails implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;
    private String endpoint;
    private List<Model> models;
    private List<EnumDto> features;
    private PriceDetails priceDetails;
}
