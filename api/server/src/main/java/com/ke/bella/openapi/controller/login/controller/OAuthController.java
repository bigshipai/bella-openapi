package com.ke.bella.openapi.controller.login.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.response.OneTokenResponse;
import com.ke.bella.openapi.controller.login.oauth.OAuthProperties;
import com.ke.bella.openapi.controller.login.oauth.OAuthService;
import com.ke.bella.openapi.controller.login.session.SessionManager;
import com.ke.bella.openapi.controller.login.session.TicketManager;

/**
 * OAuth Controller，处理 OAuth 配置查询和回调。
 */
@RestController
@RequestMapping("/openapi/oauth")
public class OAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthController.class);

    private final Map<String, OAuthService> oauthServices;
    private final SessionManager sessionManager;
    private final TicketManager ticketManager;
    private final OAuthProperties properties;

    public OAuthController(List<OAuthService> services,
                           SessionManager sessionManager,
                           TicketManager ticketManager,
                           OAuthProperties properties) {
        this.oauthServices = new HashMap<>();
        for (OAuthService service : services) {
            this.oauthServices.put(service.getProviderType(), service);
        }
        this.sessionManager = sessionManager;
        this.ticketManager = ticketManager;
        this.properties = properties;
    }

    /**
     * 获取 OAuth 配置（支持的提供商及其授权 URL）
     */
    @GetMapping("/config")
    public OneTokenResponse<List<Map<String, Object>>> oauthConfig(
            @RequestParam(required = false) String redirect) {
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

        OneTokenResponse<List<Map<String, Object>>> resp = new OneTokenResponse<>();
        resp.setCode(200);
        resp.setData(providers);
        return resp;
    }

    /**
     * OAuth 回调
     */
    @GetMapping("/callback/{provider}")
    public void callback(@PathVariable String provider,
                         @RequestParam(value = "code", required = false) String code,
                         @RequestParam(value = "state", required = false) String state,
                         @RequestParam(value = "error", required = false) String error,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        OAuthService service = oauthServices.get(provider);
        if (service == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid provider");
            return;
        }

        if (StringUtils.isNotBlank(error)) {
            LOGGER.error("{} OAuth error: {}", provider, error);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        if (!ticketManager.isValidTicket(state)) {
            LOGGER.error("OAuth validation failed - code: {}, state: {}", code, state);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state");
            return;
        }

        // 从 state 中解析出 redirect 参数
        String redirect = null;
        if (state != null && state.contains(":")) {
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
