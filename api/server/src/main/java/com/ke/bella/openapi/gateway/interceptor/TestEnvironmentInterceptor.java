package com.ke.bella.openapi.gateway.interceptor;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.controller.apikey.dto.ApikeyInfo;
import com.ke.bella.openapi.common.constant.EntityConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TestEnvironmentInterceptor implements HandlerInterceptor {
    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("test".equals(profile)) {
            ApikeyInfo apikey = EndpointContext.getApikey();
            Operator op = OneTokenContext.getOperatorIgnoreNull();
            return apikey.getOwnerType().equals(EntityConstants.SYSTEM) || (op != null && StringUtils.isNotBlank(op.getManagerAk()));
        }
        return true;
    }
}
