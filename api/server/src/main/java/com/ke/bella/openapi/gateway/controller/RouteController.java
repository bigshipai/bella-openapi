package com.ke.bella.openapi.gateway.controller;

import com.ke.bella.openapi.apikey.ApikeyInfo;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.gateway.route.ChannelRouter;
import com.ke.bella.openapi.gateway.route.RouteRequest;
import com.ke.bella.openapi.gateway.route.RouteResult;
import com.ke.bella.openapi.service.ApikeyService;
import com.ke.bella.openapi.tables.pojos.ChannelDB;
import com.ke.bella.openapi.utils.EncryptUtils;
import com.ke.bella.openapi.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ke.bella.openapi.common.annotation.OneTokenAPI;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@OneTokenAPI
@RestController
@RequestMapping("/v1/route")
@Tag(name = "Routing")
public class RouteController {
    @Autowired
    private ChannelRouter channelRouter;
    @Autowired
    private ApikeyService apikeyService;

    @PostMapping
    public RouteResult route(@RequestBody RouteRequest request) {
        String sha = EncryptUtils.sha256(request.getApikey());
        ApikeyInfo apikeyInfo = apikeyService.queryBySha(sha, true);
        if(apikeyInfo == null) {
            throw new BizParamCheckException("User API key does not exist");
        }

        ChannelDB channelDB = channelRouter.route(request.getEndpoint(), request.getModel(),
			apikeyInfo, request.getQueueMode());

        return RouteResult.builder()
                .channelCode(channelDB.getChannelCode())
                .entityType(channelDB.getEntityType())
                .entityCode(channelDB.getEntityCode())
                .protocol(channelDB.getProtocol())
                .url(channelDB.getUrl())
                .channelInfo(JacksonUtils.toMap(channelDB.getChannelInfo()))
                .priceInfo(channelDB.getPriceInfo())
                .queueMode(channelDB.getQueueMode().intValue())
                .queueName(channelDB.getQueueName())
                .build();
    }

    @PostMapping("/list")
    public List<RouteResult> listAvailableChannels(@RequestBody RouteRequest request) {
        String sha = EncryptUtils.sha256(request.getApikey());
        ApikeyInfo apikeyInfo = apikeyService.queryBySha(sha, true);
        if(apikeyInfo == null) {
            throw new BizParamCheckException("User API key does not exist");
        }

        List<ChannelDB> channels = channelRouter.listAvailableChannels(
                request.getEndpoint(),
                request.getModel(),
                apikeyInfo,
                request.getQueueMode()
        );

        return channels.stream()
                .map(channelDB -> RouteResult.builder()
                        .channelCode(channelDB.getChannelCode())
                        .entityType(channelDB.getEntityType())
                        .entityCode(channelDB.getEntityCode())
                        .protocol(channelDB.getProtocol())
                        .url(channelDB.getUrl())
                        .channelInfo(JacksonUtils.toMap(channelDB.getChannelInfo()))
                        .priceInfo(channelDB.getPriceInfo())
                        .queueMode(channelDB.getQueueMode().intValue())
                        .queueName(channelDB.getQueueName())
                        .build())
                .collect(Collectors.toList());
    }

}
