package com.ke.bella.openapi.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.response.OneTokenResponse;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.login.session.SessionManager;
import com.ke.bella.openapi.utils.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.ke.bella.openapi.login.config.LoginConfiguration.redirectParameter;

/**
 * 登录认证过滤器，基于 OncePerRequestFilter 确保每次请求只执行一次。
 * 处理 /openapi/login、/openapi/register、/openapi/logout、/openapi/userInfo 以及通用认证。
 */
public class LoginFilter extends OncePerRequestFilter {
    private final LoginProperties properties;
    private final SessionManager sessionManager;
    public static final String REDIRECT_HEADER = "X-Redirect-Login";
    public static final String CONSOLE_HEADER = "X-BELLA-CONSOLE";

    public LoginFilter(LoginProperties properties, SessionManager sessionManager) {
        this.properties = properties;
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // POST /openapi/login — 邮箱密码/密钥登录
        if ("/openapi/login".equals(request.getRequestURI()) && HttpMethod.POST.matches(request.getMethod())) {
            handleLogin(request, response);
            return;
        }

        // POST /openapi/register — 自助注册
        if ("/openapi/register".equals(request.getRequestURI()) && HttpMethod.POST.matches(request.getMethod())) {
            handleRegister(request, response);
            return;
        }

        // /openapi/logout — 登出
        if ("/openapi/logout".equals(request.getRequestURI())) {
            sessionManager.destroySession(request);
            response.setStatus(HttpStatus.OK.value());
            return;
        }

        // /openapi/userInfo — 获取当前用户信息
        if ("/openapi/userInfo".equals(request.getRequestURI())) {
            handleUserInfo(request, response);
            return;
        }

        // /openapi/* (非 /openapi/oauth) — 404
        if (request.getRequestURI().startsWith("/openapi") && !request.getRequestURI().startsWith("/openapi/oauth")) {
            writeJsonResponse(response, 404, "Not Found");
            return;
        }

        // 通用认证逻辑
        try {
            // 如果配置了 authorizationHeader，携带该 header 的请求直接放行
            if (StringUtils.isNotBlank(properties.getAuthorizationHeader())) {
                String auth = request.getHeader(properties.getAuthorizationHeader());
                if (StringUtils.isNotBlank(auth)) {
                    chain.doFilter(request, response);
                    return;
                }
            }

            Operator operator = sessionManager.getSession(request);
            if (operator != null) {
                OneTokenContext.setOperator(operator);
                chain.doFilter(request, response);
                return;
            }

            // 控制台请求且已配置登录页 → 返回 401 并设置重定向 header
            if ("true".equals(request.getHeader(CONSOLE_HEADER)) && StringUtils.isNotBlank(properties.getLoginPageUrl())) {
                response.setHeader(REDIRECT_HEADER, properties.getLoginPageUrl() + "?" + redirectParameter + "=");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 未认证但非控制台请求，继续放行（公开端点由 Interceptor 处理）
            chain.doFilter(request, response);
        } finally {
            OneTokenContext.clearAll();
            sessionManager.renew(request);
        }
    }

    // ────────────────────────────── handler methods ──────────────────────────────

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!sessionManager.userRepoInitialized()) {
            writeJsonResponse(response, 503, "Login feature not initialized");
            return;
        }
        String jsonBody = readRequestBody(request);
        if (jsonBody == null) {
            writeJsonResponse(response, 400, "Missing login parameters");
            return;
        }
        Map<String, Object> map = JacksonUtils.toMap(jsonBody);

        // 邮箱密码登录
        if (map.get("email") != null && map.get("password") != null) {
            String id = sessionManager.createByPassword(
                    map.get("email").toString(), map.get("password").toString(), request);
            if (id != null) {
                writeTokenResponse(response, id);
                return;
            }
            writeJsonResponse(response, 400, "Incorrect email or password");
            return;
        }

        // 密钥登录
        if (map.get("secret") != null) {
            String id = sessionManager.create(map.get("secret").toString(), request);
            if (id != null) {
                writeTokenResponse(response, id);
                return;
            }
        }

        writeJsonResponse(response, 400, "Missing login parameters");
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!sessionManager.userRepoInitialized()) {
            writeJsonResponse(response, 503, "Registration feature not initialized");
            return;
        }
        String jsonBody = readRequestBody(request);
        if (jsonBody == null) {
            writeJsonResponse(response, 400, "Missing registration parameters (email, password)");
            return;
        }
        Map<String, Object> map = JacksonUtils.toMap(jsonBody);
        String email = map.get("email") != null ? map.get("email").toString() : null;
        String password = map.get("password") != null ? map.get("password").toString() : null;
        String userName = map.get("userName") != null ? map.get("userName").toString() : null;

        if (email != null && password != null) {
            Operator operator = sessionManager.register(email, password, userName);
            if (operator != null) {
                // 注册成功，自动登录
                String token = sessionManager.createByPassword(email, password, request);
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", operator);
                writeJsonResponse(response, 200, data);
                return;
            }
            writeJsonResponse(response, 400, "Registration failed, email may already be in use");
            return;
        }
        writeJsonResponse(response, 400, "Missing registration parameters (email, password)");
    }

    private void handleUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Operator operator = sessionManager.getSession(request);
        if (operator != null) {
            writeJsonResponse(response, 200, operator);
        } else {
            response.setHeader(REDIRECT_HEADER, properties.getLoginPageUrl() + "?" + redirectParameter + "=");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // ────────────────────────────── helper methods ──────────────────────────────

    private String readRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void writeTokenResponse(HttpServletResponse response, String token) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        writeJsonResponse(response, 200, data);
    }

    @SuppressWarnings("rawtypes")
    private void writeJsonResponse(HttpServletResponse response, int code, Object data) throws IOException {
        OneTokenResponse<Object> oneTokenResponse = new OneTokenResponse<>();
        oneTokenResponse.setCode(code);
        if (data instanceof String) {
            oneTokenResponse.setMessage((String) data);
        } else {
            oneTokenResponse.setData(data);
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JacksonUtils.serialize(oneTokenResponse));
    }
}
