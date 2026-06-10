package com.ke.bella.openapi.controller.endpoint;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.common.annotation.EndpointAPI;
import com.ke.bella.openapi.domain.endpoint.EndpointDataService;
import com.ke.bella.openapi.domain.protocol.AdaptorManager;
import com.ke.bella.openapi.domain.route.ChannelRouter;
import com.ke.bella.openapi.domain.protocol.embedding.EmbeddingAdaptor;
import com.ke.bella.openapi.domain.protocol.embedding.EmbeddingProperty;
import com.ke.bella.openapi.domain.protocol.embedding.EmbeddingRequest;
import com.ke.bella.openapi.domain.protocol.limiter.LimiterManager;
import com.ke.bella.openapi.jooqgen.tables.pojos.ChannelDB;
import com.ke.bella.openapi.utils.JacksonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EndpointAPI
@RestController
@RequestMapping("/v1/embeddings")
@Tag(name = "Embedding Endpoint")
public class EmbeddingController {

    @Autowired
    private ChannelRouter router;
    @Autowired
    private AdaptorManager adaptorManager;
    @Autowired
    private LimiterManager limiterManager;
    @Autowired
    private EndpointDataService endpointDataService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping
    public Object embedding(@RequestBody EmbeddingRequest request) {
        String endpoint = EndpointContext.getRequest().getRequestURI();
        String model = request.getModel();
        endpointDataService.setEndpointData(endpoint, model, request);
        EndpointProcessData processData = EndpointContext.getProcessData();
        ChannelDB channel = router.route(endpoint, model, EndpointContext.getApikey(), processData.isMock());
        endpointDataService.setChannel(channel);
        if(!EndpointContext.getProcessData().isPrivate()) {
            limiterManager.incrementConcurrentCount(EndpointContext.getProcessData().getAkCode(), model);
        }
        String protocol = processData.getProtocol();
        String url = processData.getForwardUrl();
        String channelInfo = channel.getChannelInfo();
        EmbeddingAdaptor adaptor = adaptorManager.getProtocolAdaptor(endpoint, protocol, EmbeddingAdaptor.class);
        EmbeddingProperty property = (EmbeddingProperty) JacksonUtils.deserialize(channelInfo, adaptor.getPropertyClass());
        EndpointContext.setEncodingType(property.getEncodingType());
        return adaptor.embedding(request, url, property);
    }
}
