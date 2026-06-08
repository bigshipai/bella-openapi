package com.ke.bella.openapi.request;

import com.ke.bella.openapi.BellaContext;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * 请求级别的 OncePerRequestFilter，负责提取 X-BELLA-* 头信息和生成 trace/request ID。
 * 子类可覆盖 {@link #doFilterInternal} 添加自定义认证逻辑。
 */
public class BellaRequestFilter extends OncePerRequestFilter {
    private final String serviceId;

    public BellaRequestFilter(String serviceId) {
        Assert.hasText(serviceId, "serviceId不能为空");
        this.serviceId = serviceId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            bellaRequestFilter(request, response);
            filterChain.doFilter(request, response);
        } finally {
            BellaContext.clearAll();
        }
    }

    /**
     * 提取 X-BELLA-* 自定义头，生成 trace ID 和 request ID。
     */
    protected void bellaRequestFilter(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toUpperCase();
            if (headerName.startsWith("X-BELLA-")) {
                BellaContext.getHeaders().put(headerName, request.getHeader(headerName));
            }
        }

        String bellaTraceId = BellaContext.getTraceId();
        if (bellaTraceId == null) {
            bellaTraceId = BellaContext.generateTraceId(serviceId);
            BellaContext.getHeaders().put(BellaContext.BELLA_TRACE_HEADER, bellaTraceId);
        }
        response.addHeader(BellaContext.BELLA_TRACE_HEADER, bellaTraceId);

        String requestId = UUID.randomUUID().toString();
        BellaContext.getHeaders().put(BellaContext.BELLA_REQUEST_ID_HEADER, requestId);
        response.addHeader(BellaContext.BELLA_REQUEST_ID_HEADER, requestId);
    }
}
