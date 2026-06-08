package com.ke.bella.openapi.intercept;

import com.ke.bella.openapi.BellaContext;
import com.ke.bella.openapi.EndpointContext;
import com.ke.bella.openapi.apikey.ApikeyInfo;
import com.ke.bella.openapi.request.BellaRequestFilter;
import com.ke.bella.openapi.service.ApikeyService;
import com.ke.bella.openapi.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OpenAPI 请求过滤器，负责 API Key 认证和请求/响应日志记录。
 * 构造函数注入 ApikeyService，消除 HTTP 自调用。
 */
@Slf4j
@Component
public class OpenapiRequestFilter extends BellaRequestFilter {

    private final ApikeyService apikeyService;

    public OpenapiRequestFilter(ApikeyService apikeyService) {
        super("openapi");
        this.apikeyService = apikeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            // 直接验证 API Key，不走 HTTP 自调用
            String auth = request.getHeader("Authorization");
            if (auth != null && StringUtils.isNotBlank(auth)) {
                ApikeyInfo apikeyInfo = verifyAuthHeader(auth);
                if (apikeyInfo != null) {
                    BellaContext.setApikey(apikeyInfo);
                }
            }
            super.bellaRequestFilter(request, response);
            log.info("[traceId={}] Request  : {} {}", BellaContext.getTraceId(), request.getMethod(), request.getRequestURI());
            EndpointContext.setHeaderInfo(BellaContext.getHeaders());
            EndpointContext.getProcessData().setRequestMillis(DateTimeUtils.getCurrentMills());
            EndpointContext.getProcessData().setRequestTime(DateTimeUtils.getCurrentSeconds());
            EndpointContext.setRequest(request);
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            String akCode = EndpointContext.getProcessData().getAkCode();
            log.info("[traceId={}][akCode={}] Response : {} {} status={} cost={}ms",
                    BellaContext.getTraceId(),
                    akCode != null ? akCode : "-",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    cost);
            // EndpointContext.clearAll() 内部会调用 BellaContext.clearAll()，统一清理所有 ThreadLocal
            EndpointContext.clearAll();
        }
    }

    /**
     * 直接注入 ApikeyService 验证 API Key，消除 HTTP 自调用（原 OpenapiClient 方式）。
     */
    private ApikeyInfo verifyAuthHeader(String auth) {
        String ak;
        if (auth.startsWith("Bearer ")) {
            ak = auth.substring(7);
        } else if (!auth.contains(" ")) {
            ak = auth;
        } else {
            return null;
        }
        try {
            return apikeyService.verifyAuth(ak);
        } catch (Exception e) {
            log.warn("API key verification failed: {}", e.getMessage());
            return null;
        }
    }
}
