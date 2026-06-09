package com.ke.bella.openapi.modules.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.common.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Endpoint extends BaseDto {
    private String endpoint;
    private String endpointCode;
    private String endpointName;
    private String maintainerCode;
    private String maintainerName;
    private String status;
}
