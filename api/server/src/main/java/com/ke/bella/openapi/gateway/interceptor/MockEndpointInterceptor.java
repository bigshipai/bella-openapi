package com.ke.bella.openapi.gateway.interceptor;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.OneTokenContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MockEndpointInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("true".equals(request.getHeader(OneTokenContext.BELLA_REQUEST_MOCK_HEADER))) {
            EndpointContext.getProcessData().setMock(true);
        }
        return true;
    }
}
