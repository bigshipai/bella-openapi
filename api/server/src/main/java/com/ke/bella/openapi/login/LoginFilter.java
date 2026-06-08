package com.ke.bella.openapi.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ke.bella.openapi.BellaContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.ke.bella.openapi.BellaResponse;
import com.ke.bella.openapi.Operator;
import com.ke.bella.openapi.login.session.SessionManager;
import com.ke.bella.openapi.utils.JacksonUtils;

import static com.ke.bella.openapi.login.config.LoginConfiguration.redirectParameter;

public class LoginFilter implements Filter {
    private final LoginProperties properties;
    private final SessionManager sessionManager;
    public static final String REDIRECT_HEADER = "X-Redirect-Login";
    public static final String CONSOLE_HEADER = "X-BELLA-CONSOLE";

    public LoginFilter(LoginProperties properties, SessionManager sessionManager) {
        this.properties = properties;
        this.sessionManager = sessionManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if("/openapi/login".equals(httpRequest.getRequestURI()) && httpRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            if(!sessionManager.userRepoInitialized()) {
                BellaResponse<Boolean> bellaResponse = new BellaResponse<>();
                bellaResponse.setCode(503);
                bellaResponse.setMessage("登录功能未初始化");
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().write(JacksonUtils.serialize(bellaResponse));
                return;
            }
            String jsonBody = readRequestBody(httpRequest);
            if(jsonBody != null) {
                Map<String, Object> map = JacksonUtils.toMap(jsonBody);

                // 邮箱密码登录
                if(map.get("email") != null && map.get("password") != null) {
                    String id = sessionManager.createByPassword(
                            map.get("email").toString(), map.get("password").toString(), httpRequest);
                    if(id != null) {
                        writeTokenResponse(response, id);
                        return;
                    }
                    BellaResponse<Boolean> errorResp = new BellaResponse<>();
                    errorResp.setCode(400);
                    errorResp.setMessage("邮箱或密码错误");
                    response.setContentType("application/json; charset=utf-8");
                    response.getWriter().write(JacksonUtils.serialize(errorResp));
                    return;
                }

                // 密钥登录
                if(map.get("secret") != null) {
                    String id = sessionManager.create(map.get("secret").toString(), httpRequest);
                    if(id != null) {
                        writeTokenResponse(response, id);
                        return;
                    }
                }
            }
            BellaResponse<Boolean> bellaResponse = new BellaResponse<>();
            bellaResponse.setCode(400);
            bellaResponse.setMessage("缺少登录参数");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JacksonUtils.serialize(bellaResponse));
            return;
        }
        if("/openapi/register".equals(httpRequest.getRequestURI()) && httpRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            if(!sessionManager.userRepoInitialized()) {
                BellaResponse<Boolean> bellaResponse = new BellaResponse<>();
                bellaResponse.setCode(503);
                bellaResponse.setMessage("注册功能未初始化");
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().write(JacksonUtils.serialize(bellaResponse));
                return;
            }
            String jsonBody = readRequestBody(httpRequest);
            if(jsonBody != null) {
                Map<String, Object> map = JacksonUtils.toMap(jsonBody);
                String email = map.get("email") != null ? map.get("email").toString() : null;
                String password = map.get("password") != null ? map.get("password").toString() : null;
                String userName = map.get("userName") != null ? map.get("userName").toString() : null;
                if(email != null && password != null) {
                    Operator operator = sessionManager.register(email, password, userName);
                    if(operator != null) {
                        // 注册成功，自动登录
                        String token = sessionManager.createByPassword(email, password, httpRequest);
                        Map<String, Object> data = new HashMap<>();
                        data.put("token", token);
                        data.put("user", operator);
                        BellaResponse<Map<String, Object>> bellaResponse = new BellaResponse<>();
                        bellaResponse.setCode(200);
                        bellaResponse.setData(data);
                        response.setContentType("application/json; charset=utf-8");
                        response.getWriter().write(JacksonUtils.serialize(bellaResponse));
                        return;
                    }
                    BellaResponse<Boolean> errorResp = new BellaResponse<>();
                    errorResp.setCode(400);
                    errorResp.setMessage("注册失败，邮箱可能已被使用");
                    response.setContentType("application/json; charset=utf-8");
                    response.getWriter().write(JacksonUtils.serialize(errorResp));
                    return;
                }
            }
            BellaResponse<Boolean> bellaResponse = new BellaResponse<>();
            bellaResponse.setCode(400);
            bellaResponse.setMessage("缺少注册参数（email, password）");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JacksonUtils.serialize(bellaResponse));
            return;
        }
        if("/openapi/logout".equals(httpRequest.getRequestURI())) {
            sessionManager.destroySession(httpRequest);
            httpResponse.setStatus(HttpStatus.OK.value());
            return;
        }
        if("/openapi/userInfo".equals(httpRequest.getRequestURI())) {
            Operator operator = sessionManager.getSession(httpRequest);
            if(operator != null) {
                BellaResponse<Operator> bellaResponse = new BellaResponse<>();
                bellaResponse.setCode(200);
                bellaResponse.setData(operator);
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().write(JacksonUtils.serialize(bellaResponse));
            } else {
                httpResponse.setHeader(REDIRECT_HEADER, properties.getLoginPageUrl() + "?" + redirectParameter + "=");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }

        if(httpRequest.getRequestURI().startsWith("/openapi") && !httpRequest.getRequestURI().startsWith("/openapi/oauth")) {
            BellaResponse<?> bellaResponse = new BellaResponse<>();
            bellaResponse.setCode(404);
            bellaResponse.setMessage("Not Found");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JacksonUtils.serialize(bellaResponse));
            return;
        }

        try {
            if(StringUtils.isNotBlank(properties.getAuthorizationHeader())) {
                String auth = httpRequest.getHeader(properties.getAuthorizationHeader());
                if(StringUtils.isNotBlank(auth)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
            Operator operator = sessionManager.getSession(httpRequest);
            if(operator != null) {
                BellaContext.setOperator(operator);
                chain.doFilter(request, response);
                return;
            }
            if("true".equals(httpRequest.getHeader(CONSOLE_HEADER)) && StringUtils.isNotBlank(properties.getLoginPageUrl())) {
                httpResponse.setHeader(REDIRECT_HEADER, properties.getLoginPageUrl() + "?" + redirectParameter + "=");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            chain.doFilter(request, response);
        } finally {
            BellaContext.clearAll();
            sessionManager.renew(httpRequest);
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void writeTokenResponse(ServletResponse response, String token) throws IOException {
        BellaResponse<Map<String, String>> bellaResponse = new BellaResponse<>();
        bellaResponse.setCode(200);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        bellaResponse.setData(data);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JacksonUtils.serialize(bellaResponse));
    }
}
