package com.ke.bella.openapi.gateway.filter;

import com.ke.bella.openapi.common.context.OneTokenContext;
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
        Assert.hasText(serviceId, "serviceId cannot be empty");
        this.serviceId = serviceId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            bellaRequestFilter(request, response);
            filterChain.doFilter(request, response);
        } finally {
            OneTokenContext.clearAll();
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
                OneTokenContext.getHeaders().put(headerName, request.getHeader(headerName));
            }
        }

        String bellaTraceId = OneTokenContext.getTraceId();
        if (bellaTraceId == null) {
            bellaTraceId = OneTokenContext.generateTraceId(serviceId);
            OneTokenContext.getHeaders().put(OneTokenContext.BELLA_TRACE_HEADER, bellaTraceId);
        }
        response.addHeader(OneTokenContext.BELLA_TRACE_HEADER, bellaTraceId);

        String requestId = UUID.randomUUID().toString();
        OneTokenContext.getHeaders().put(OneTokenContext.BELLA_REQUEST_ID_HEADER, requestId);
        response.addHeader(OneTokenContext.BELLA_REQUEST_ID_HEADER, requestId);
    }
}
