package com.ke.bella.openapi.controller.meta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.controller.endpoint.dto.Endpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class EndpointCategoryTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private String categoryCode;
    private String categoryName;
    private List<Endpoint> endpoints;
    private List<EndpointCategoryTree> children;

    public void addChild(EndpointCategoryTree tree) {
        if(children == null) {
            children = new ArrayList<>();
        }
        children.add(tree);
    }
}
