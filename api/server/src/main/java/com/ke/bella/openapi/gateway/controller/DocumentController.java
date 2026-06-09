package com.ke.bella.openapi.gateway.controller;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.TaskExecutor;
import com.ke.bella.openapi.common.annotation.EndpointAPI;
import com.ke.bella.openapi.protocol.AdaptorManager;
import com.ke.bella.openapi.gateway.route.ChannelRouter;
import com.ke.bella.openapi.protocol.document.parse.DocParseAdaptor;
import com.ke.bella.openapi.protocol.document.parse.DocParseCallbackService;
import com.ke.bella.openapi.protocol.document.parse.DocParseProperty;
import com.ke.bella.openapi.protocol.document.parse.DocParseRequest;
import com.ke.bella.openapi.protocol.document.parse.DocParseResponse;
import com.ke.bella.openapi.protocol.document.parse.TaskIdUtils;
import com.ke.bella.openapi.protocol.limiter.LimiterManager;
import com.ke.bella.openapi.modules.endpoint.EndpointDataService;
import com.ke.bella.openapi.generated.tables.pojos.ChannelDB;
import com.ke.bella.openapi.utils.JacksonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EndpointAPI
@RestController
@RequestMapping("/v1/document")
@Tag(name = "document")
@Slf4j
public class DocumentController {

    @Autowired
    private ChannelRouter channelRouter;
    @Autowired
    private AdaptorManager adaptorManager;
    @Autowired
    private LimiterManager limiterManager;
    @Autowired
    private EndpointDataService endpointDataService;
    @Autowired
    private DocParseCallbackService docParseCallbackService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/parse")
    public Object parse(@RequestBody DocParseRequest request) {
        String endpoint = EndpointContext.getRequest().getRequestURI();
        String model = request.getModel();
        endpointDataService.setEndpointData(endpoint, model, request);
        EndpointProcessData processData = EndpointContext.getProcessData();
        ChannelDB channel = channelRouter.route(endpoint, model, EndpointContext.getApikey(), processData.isMock());
        endpointDataService.setChannel(channel);
        if(!EndpointContext.getProcessData().isPrivate()) {
            limiterManager.incrementConcurrentCount(EndpointContext.getProcessData().getAkCode(), model);
        }
        String protocol = processData.getProtocol();
        String url = processData.getForwardUrl();
        String channelInfo = channel.getChannelInfo();
        DocParseAdaptor adaptor = adaptorManager.getProtocolAdaptor(endpoint, protocol, DocParseAdaptor.class);
        DocParseProperty property = (DocParseProperty) JacksonUtils.deserialize(channelInfo, adaptor.getPropertyClass());
        return adaptor.parse(request, url, channel.getChannelCode(), property, docParseCallbackService);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("/parse")
    public DocParseResponse queryParseResult(@RequestParam("task_id") String taskId) {
        String endpoint = EndpointContext.getRequest().getRequestURI();
        String[] taskInfo = TaskIdUtils.extractTaskId(taskId);
        String channelCode = taskInfo[0];
        ChannelDB channel = channelRouter.route(channelCode);
        endpointDataService.setChannel(channel);
        EndpointProcessData processData = EndpointContext.getProcessData();
        String protocol = processData.getProtocol();
        String url = processData.getForwardUrl();
        String channelInfo = channel.getChannelInfo();
        DocParseAdaptor adaptor = adaptorManager.getProtocolAdaptor(endpoint, protocol, DocParseAdaptor.class);
        DocParseProperty property = (DocParseProperty) JacksonUtils.deserialize(channelInfo, adaptor.getPropertyClass());
        DocParseResponse response = adaptor.queryResult(taskInfo[1], url, property);
        if(response.getCallback() != null) {
            TaskExecutor.submit(response.getCallback());
        }
        return response;
    }
}
