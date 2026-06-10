package com.ke.bella.openapi.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ConcurrentStartInterceptor implements HandlerInterceptor {
	public static final String ASYNC_REQUEST_MARKER = "ASYNC_REQUEST_MARKER";

	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
		request.setAttribute(ASYNC_REQUEST_MARKER, Boolean.TRUE);
	}
}
