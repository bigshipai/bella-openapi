package com.ke.bella.openapi.config.interceptor;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.OneTokenContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MockEndpointInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
							 @NotNull Object handler) throws Exception {
		if ("true".equals(request.getHeader(OneTokenContext.BELLA_REQUEST_MOCK_HEADER))) {
			EndpointContext.getProcessData().setMock(true);
		}
		return true;
	}
}
