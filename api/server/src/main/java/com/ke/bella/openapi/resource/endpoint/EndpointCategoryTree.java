package com.ke.bella.openapi.resource.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import lombok.AllArgsConstructor;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import lombok.Builder;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import lombok.Data;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import lombok.NoArgsConstructor;
import com.ke.bella.openapi.resource.endpoint.Endpoint;

import java.io.Serializable;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import java.util.ArrayList;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import java.util.List;
import com.ke.bella.openapi.resource.endpoint.Endpoint;

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
