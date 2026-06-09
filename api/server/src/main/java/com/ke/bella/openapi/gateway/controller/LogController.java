package com.ke.bella.openapi.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.modules.apikey.ApikeyInfo;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.protocol.log.EndpointLogger;
import com.ke.bella.openapi.service.ApikeyService;

import io.swagger.v3.oas.annotations.tags.Tag;

@OneTokenAPI
@RestController
@RequestMapping("/v1/log")
@Tag(name = "Logs")
public class LogController {
    @Autowired
    private EndpointLogger logger;
    @Autowired
    private ApikeyService apikeyService;

    @PostMapping
    public Boolean record(@RequestBody EndpointProcessData processData) {
        Assert.hasText(processData.getEndpoint(), "endpoint can not be null");
        Assert.hasText(processData.getAkSha(), "akSha sha can not be null");
        Assert.hasText(processData.getBellaTraceId(), "bella trace id can not be null");
        ApikeyInfo apikeyInfo = apikeyService.queryBySha(processData.getAkSha(), true);
        if(apikeyInfo == null) {
            throw new BizParamCheckException("User API key does not exist");
        }
        processData.setApikeyInfo(apikeyInfo);
        processData.setInnerLog(false);
        logger.log(processData);
        return true;
    }
}
