package com.ke.bella.openapi.intercept;

import com.ke.bella.openapi.EndpointContext;
import com.ke.bella.openapi.BellaContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MockEndpointInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("true".equals(request.getHeader(BellaContext.BELLA_REQUEST_MOCK_HEADER))) {
            EndpointContext.getProcessData().setMock(true);
        }
        return true;
    }
}
