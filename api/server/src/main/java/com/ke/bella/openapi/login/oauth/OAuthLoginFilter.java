package com.ke.bella.openapi.login.oauth;

import com.ke.bella.openapi.BellaResponse;
import com.ke.bella.openapi.Operator;
import com.ke.bella.openapi.login.session.SessionManager;
import com.ke.bella.openapi.login.session.TicketManager;
import com.ke.bella.openapi.utils.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth 登录过滤器，基于 OncePerRequestFilter 确保每次请求只执行一次。
 * 仅处理 /openapi/oauth/callback/* 和 /openapi/oauth/config 路径。
 */
public class OAuthLoginFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthLoginFilter.class);

    private final Map<String, OAuthService> oauthServices;
    private final SessionManager sessionManager;
    private final TicketManager ticketManager;
    private final OAuthProperties properties;

    public OAuthLoginFilter(List<OAuthService> services, SessionManager sessionManager,
                            TicketManager ticketManager, OAuthProperties properties) {
        this.ticketManager = ticketManager;
        this.oauthServices = new HashMap<>();
        for (OAuthService service : services) {
            this.oauthServices.put(service.getProviderType(), service);
        }
        this.sessionManager = sessionManager;
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/openapi/oauth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        // OAuth 回调
        if (requestUri.startsWith("/openapi/oauth/callback/")) {
            String provider = requestUri.substring("/openapi/oauth/callback/".length());
            handleCallback(provider, request, response);
            return;
        }

        // OAuth 配置
        if ("/openapi/oauth/config".equals(requestUri)) {
            handleOAuthConfig(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleOAuthConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirect = request.getParameter("redirect");
        // 将 redirect 参数编码到 state 中
        String state = UUID.randomUUID() + (StringUtils.isNotBlank(redirect) ? ":" + redirect : "");
        ticketManager.saveTicket(state);

        List<Map<String, Object>> providers = new ArrayList<>();
        for (String type : oauthServices.keySet()) {
            OAuthService service = oauthServices.get(type);
            String authUrl = service.getAuthorizationUrl(state);

            Map<String, Object> provider = new HashMap<>();
            provider.put("type", service.getProviderType());
            provider.put("authUrl", authUrl);
            providers.add(provider);
        }

        BellaResponse<List<Map<String, Object>>> bellaResponse = new BellaResponse<>();
        bellaResponse.setCode(200);
        bellaResponse.setData(providers);
        response.setContentType("application/json");
        response.getWriter().write(JacksonUtils.serialize(bellaResponse));
    }

    private void handleCallback(String provider, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OAuthService service = oauthServices.get(provider);
        if (service == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid provider");
            return;
        }

        String error = request.getParameter("error");
        if (StringUtils.isNotBlank(error)) {
            LOGGER.error("{} OAuth error: {}", provider, error);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        String code = request.getParameter("code");
        String state = request.getParameter("state");

        if (!ticketManager.isValidTicket(state)) {
            LOGGER.error("OAuth validation failed - code: {}, state: {}", code, state);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state");
            return;
        }

        // 从 state 中解析出 redirect 参数
        String redirect = null;
        if (state.contains(":")) {
            redirect = state.substring(state.indexOf(":") + 1);
        }

        Operator operator = service.handleCallback(code, state);
        if (operator == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to get user info");
            return;
        }
        String token = sessionManager.create(operator, request);
        ticketManager.removeTicket(state);
        String redirectUrl = StringUtils.isNotBlank(redirect) ? redirect : properties.getClientIndex();
        if (StringUtils.isNotBlank(token)) {
            redirectUrl = redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "token=" + token;
        }
        response.sendRedirect(redirectUrl);
    }
}
